package acme.features.administrator.weather;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;


@Repository
public interface AdministratorWeatherRepository extends AbstractRepository{
	
	@Query("SELECT DISTINCT(a.city) FROM Airport a")
	List<String> findAllCities();
	
}
