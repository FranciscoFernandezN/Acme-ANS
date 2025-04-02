
package acme.features.manager.dashboard;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@Repository
public interface ManagerDashboardRepository extends AbstractRepository {

	@Query("SELECT m FROM Manager m ORDER BY m.yearsOfExperience DESC")
	List<Manager> rankingByYearsOfExperience();

	@Query("SELECT 1.0 * COUNT(l1) / (SELECT COUNT(l2) FROM Leg l2 WHERE l2.manager.id = :managerId) FROM Leg l1 WHERE l1.status LIKE 'ON_TIME' AND l1.manager.id = :managerId")
	Double ratioOfOnTimeLegs(int managerId);

	@Query("SELECT 1.0 * COUNT(l1) / (SELECT COUNT(l2) FROM Leg l2 WHERE l2.manager.id = :managerId) FROM Leg l1 WHERE l1.status LIKE 'DELAYED' AND l1.manager.id = :managerId")
	Double ratioOfDelayedLegs(int managerId);

	@Query("SELECT f FROM Flight f WHERE f.manager.id = :managerId")
	List<Flight> findAllFlightsByManagerId(int managerId);

	@Query("SELECT COUNT(l) FROM Leg l WHERE l.manager.id = :managerId AND l.status LIKE 'ON_TIME'")
	Long numberOfOnTimeLegs(int managerId);
	
	@Query("SELECT COUNT(l) FROM Leg l WHERE l.manager.id = :managerId AND l.status LIKE 'CANCELLED'")
	Long numberOfCancelledLegs(int managerId);
	
	@Query("SELECT COUNT(l) FROM Leg l WHERE l.manager.id = :managerId AND l.status LIKE 'DELAYED'")
	Long numberOfDelayedLegs(int managerId);
	
	@Query("SELECT COUNT(l) FROM Leg l WHERE l.manager.id = :managerId AND l.status LIKE 'LANDED'")
	Long numberOfLandedLegs(int managerId);

}
