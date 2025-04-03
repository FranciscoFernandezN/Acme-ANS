package acme.features.any.service;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.services.Service;

public interface AnyServiceRepository extends AbstractRepository{
	
	@Query("SELECT s FROM Service s")
	List<Service> findAllServices();
	
	@Query("SELECT s FROM Service s WHERE s.id = :id")
	Service findServiceById(int id);
	
}
