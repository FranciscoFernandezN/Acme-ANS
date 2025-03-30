
package acme.features.administrator.service;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airports.Airport;
import acme.entities.services.Service;

@Repository
public interface AdministratorServiceRepository extends AbstractRepository {

	@Query("select s from Service s")
	List<Service> findAllServices();

	@Query("select s from Service s where s.id = :id")
	Service findServiceById(int id);

	@Query("select a from Airport a")
	List<Airport> findAllAirports();

	@Query("select a from Airport a where a.id = :id")
	Airport findAirportById(int id);

	@Query("select s.promotionCode from Service s")
	List<String> findPromotionCodes();

}
