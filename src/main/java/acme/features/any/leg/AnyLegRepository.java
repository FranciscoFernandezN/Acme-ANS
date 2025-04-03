package acme.features.any.leg;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface AnyLegRepository extends AbstractRepository{
	
	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(int id);
	
	@Query("SELECT l FROM Leg l WHERE l.flight.id = :id ORDER BY l.scheduledDeparture ASC")
	List<Leg> findAllLegsByFlightId(int id);
	
	@Query("SELECT l FROM Leg l WHERE l.id = :id")
	Leg findLegById(int id);
	
}
