
package acme.entities.flights;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;
import acme.entities.weather.Weather;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :id")
	List<Leg> findAllLegsByFlightId(int id);
	
	@Query("SELECT w FROM Weather w WHERE w.city = :city")
	List<Weather> findWeatherByCity(String city);

}
