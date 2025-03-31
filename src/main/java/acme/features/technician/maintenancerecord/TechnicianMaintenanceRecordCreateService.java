
package acme.features.technician.maintenancerecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecords.MaintenanceRecord;
import acme.entities.maintenancerecords.MaintenanceStatus;
import acme.entities.tasks.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Technician.class));
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setMoment(new Date());
		maintenanceRecord.setStatus(MaintenanceStatus.PENDING);  // Default status, can be modified

		// Adding tasks associated with the maintenance record (if any)
		Collection<Task> tasks = this.repository.findTasksByMaintenanceRecordId(null); // Tasks will be empty initially

		super.getBuffer().addData(maintenanceRecord);
		super.getBuffer().addData(tasks);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		// Bind incoming data from the user interface
		super.bindObject(maintenanceRecord, "moment", "status", "nextInspectionDue", "estimatedCost", "notes");

		// Get the task IDs from the request and associate them with the maintenance record
		Collection<Task> tasks = super.getRequest().getData("tasks", Collection.class); // Assuming tasks are sent as IDs or list
		maintenanceRecord.setTasks(tasks);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		super.state(maintenanceRecord.getTasks().size() > 0, "tasks", "technician.maintenancerecord.create.no-tasks");

		// Ensure there is at least one task published before allowing the record to be published
		boolean hasPublishedTask = maintenanceRecord.getTasks().stream().anyMatch(task -> task.getStatus() == TaskStatus.PUBLISHED);  // Assuming TaskStatus is an enum with PUBLISHED status

		super.state(hasPublishedTask, "tasks", "technician.maintenancerecord.create.must-have-published-task");
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset = super.unbindObject(maintenanceRecord, "moment", "status", "nextInspectionDue", "estimatedCost", "notes");

		// Get tasks for this maintenance record to show in the UI
		SelectChoices taskChoices = new SelectChoices();
		Collection<Task> tasks = this.repository.findTasksByMaintenanceRecordId(maintenanceRecord.getId());
		tasks.forEach(task -> taskChoices.add(String.valueOf(task.getId()), task.getDescription(), task.getStatus() == TaskStatus.PUBLISHED));

		dataset.put("tasks", taskChoices);

		super.getResponse().addData(dataset);
	}
}
