
package acme.features.any.airport;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airports.Airport;

@Repository
public interface AnyAirportRepository extends AbstractRepository {

	@Query("select a from Airport a")
	List<Airport> findAllAirports();

	@Query("select a from Airport a where a.id = :id")
	Airport findAirportById(int id);

}
