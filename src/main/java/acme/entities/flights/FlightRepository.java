
package acme.entities.flights;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :id")
	List<Leg> findAllLegsByFlightId(int id);

}
