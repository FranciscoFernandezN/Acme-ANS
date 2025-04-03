package acme.features.any.flight;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.weather.Weather;

@Repository
public interface AnyFlightRepository extends AbstractRepository{
	
	@Query("SELECT f FROM Flight f WHERE f.isDraftMode = false")
	List<Flight> findAllFlightsPosted();
	
	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(int id);
	
	@Query("SELECT l FROM Leg l WHERE l.flight.id = :id")
	List<Leg> findAllLegsByFlightId(int id);
	
}
