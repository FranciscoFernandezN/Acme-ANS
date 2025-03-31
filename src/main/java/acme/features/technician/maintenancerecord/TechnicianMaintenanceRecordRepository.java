
package acme.features.technician.maintenancerecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.maintenancerecords.MaintenanceRecord;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select m from MaintenanceRecord m where m.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select m from MaintenanceRecord m where m.aircraft.id = :aircraftId")
	Collection<MaintenanceRecord> findMaintenanceRecordsByAircraftId(int aircraftId);

	@Query("select m from MaintenanceRecord m where m.aircraft.id in (select a.id from Aircraft a where a.technician.id = :technicianId)")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTechnicianId(int technicianId);

	@Query("select a from Aircraft a where a.id = :id")
	Aircraft findAircraftById(int id);
}
