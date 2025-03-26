
package acme.features.manager.leg;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.id = :id")
	Leg findLegById(int id);

	@Query("SELECT l FROM Leg l WHERE l.manager.id = :id")
	List<Leg> findAllLegsByManagerId(int id);

	@Query("SELECT l FROM Leg l")
	List<Leg> findAllLegs();

	@Query("SELECT a FROM Airport a WHERE a.id = :id")
	Airport findAirportById(int id);

	@Query("SELECT a FROM Airport a")
	List<Airport> findAllAirports();

	@Query("SELECT a FROM Aircraft a WHERE a.airline.id = :id")
	List<Aircraft> findAllAircraftsByAirlineId(int id);

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("SELECT l FROM Leg l WHERE l.aircraft.id = :id")
	List<Leg> findAllLegsOfAircraftByAircraftId(int id);

	@Query("SELECT f FROM Flight f WHERE f.isDraftMode = true AND f.manager.id = :id")
	List<Flight> findAllFlightsEditableByManagerId(int id);

	@Query("SELECT f FROM Flight f WHERE f.manager.id = :id")
	List<Flight> findAllFlightsByManagerId(int id);

	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(int id);

}
