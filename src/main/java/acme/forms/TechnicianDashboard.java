
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.aircrafts.Aircraft;
import acme.entities.maintenancerecords.MaintenanceRecord;
import acme.entities.maintenancerecords.MaintenanceStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long				serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	private Map<MaintenanceStatus, Integer>	maintenanceRecordsByStatus;
	private MaintenanceRecord				nextDueInspectionRecord;
	private List<Aircraft>					topFiveAircraftByMaintenanceTasks;
	private Double							avgEstimatedCostLastYear;
	private Double							minEstimatedCostLastYear;
	private Double							maxEstimatedCostLastYear;
	private Double							stdDeviationEstimatedCostLastYear;
	private Double							avgTaskDuration;
	private Double							minTaskDuration;
	private Double							maxTaskDuration;
	private Double							stdDeviationTaskDuration;
}
