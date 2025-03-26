
package acme.features.manager.leg;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.id = :id")
	Leg findLegById(int id);

	@Query("SELECT l FROM Leg l WHERE l.manager.id = :id")
	List<Leg> findAllLegsByManagerId(int id);

	@Query("SELECT l.uniqueIdentifier FROM Leg l")
	List<String> findAllLegUniqueIds();

	@Query("SELECT a FROM Airport a WHERE a.id = :id")
	Airport findAirportById(int id);

	@Query("SELECT a FROM Airport a")
	List<Airport> findAllAirports();

	@Query("SELECT a FROM Aircraft a WHERE a.airline.id = :id")
	List<Aircraft> findAllAircraftsByAirlineId(int id);

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findAircraftById(int id);

}
