package acme.features.any.weather;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.weather.Weather;

@Repository
public interface AnyWeatherRepository extends AbstractRepository{
	
	@Query("SELECT w FROM Weather w ORDER BY w.forecastDate DESC")
	List<Weather> findAllWeather();
	
	@Query("SELECT w FROM Weather w WHERE w.id = :id")
	Weather findWeatherById(int id);
	
}
