
package acme.features.manager.flight;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("SELECT f FROM Flight f WHERE f.manager.id = :id")
	List<Flight> findAllFlightsByManagerId(int id);

	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(int id);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :id")
	List<Leg> findAllLegsByFlightId(int id);

}
