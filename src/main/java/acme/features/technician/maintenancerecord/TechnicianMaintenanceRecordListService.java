
package acme.features.technician.maintenancerecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecords.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordListService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository	repository;

	@Autowired
	private TechnicianTaskRepository				taskRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<MaintenanceRecord> maintenanceRecords;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		maintenanceRecords = this.repository.findMaintenanceRecordsByTechnicianId(technicianId);

		super.getBuffer().addData(maintenanceRecords);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;
		int taskCount;

		taskCount = this.taskRepository.countTasksByMaintenanceRecordId(maintenanceRecord.getId());

		dataset = super.unbindObject(maintenanceRecord, "moment", "status", "nextInspectionDue", "estimatedCost", "notes");
		dataset.put("aircraft", maintenanceRecord.getAircraft().getModel());
		dataset.put("taskCount", taskCount);

		super.getResponse().addData(dataset);
	}
}
