
package acme.features.technician.maintenancerecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecords.MaintenanceRecord;
import acme.entities.tasks.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int recordId;
		MaintenanceRecord record;

		recordId = super.getRequest().getData("id", int.class);
		record = this.repository.findMaintenanceRecordById(recordId);
		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && super.getRequest().getPrincipal().getRealmOfType(Technician.class).getId() == record.getAircraft().getTechnician().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord record;
		int recordId;

		recordId = super.getRequest().getData("id", int.class);
		record = this.repository.findMaintenanceRecordById(recordId);

		super.getBuffer().addData(record);
	}

	@Override
	public void bind(final MaintenanceRecord record) {
		super.bindObject(record, "moment", "status", "nextInspectionDue", "estimatedCost", "notes");
	}

	@Override
	public void validate(final MaintenanceRecord record) {
		Collection<Task> relatedTasks = this.repository.findTasksByMaintenanceRecordId(record.getId());
		boolean hasPublishedTask = relatedTasks.stream().anyMatch(Task::isPublished);
		boolean hasUnpublishedTask = relatedTasks.stream().anyMatch(task -> !task.isPublished());

		super.state(hasPublishedTask, "status", "technician.maintenancerecord.update.must-have-published-task");
		super.state(!hasUnpublishedTask, "status", "technician.maintenancerecord.update.cannot-have-unpublished-task");
	}

	@Override
	public void perform(final MaintenanceRecord record) {
		this.repository.save(record);
	}

	@Override
	public void unbind(final MaintenanceRecord record) {
		Dataset dataset;
		Collection<Task> relatedTasks = this.repository.findTasksByMaintenanceRecordId(record.getId());
		boolean hasPublishedTask = relatedTasks.stream().anyMatch(Task::isPublished);

		dataset = super.unbindObject(record, "moment", "status", "nextInspectionDue", "estimatedCost", "notes");
		dataset.put("hasPublishedTask", hasPublishedTask);
		super.getResponse().addData(dataset);
	}
}
