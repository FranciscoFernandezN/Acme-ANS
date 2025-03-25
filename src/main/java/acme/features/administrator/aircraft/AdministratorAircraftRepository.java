
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;

@Repository
public interface AdministratorAircraftRepository extends AbstractRepository {

	@Query("select c from Aircraft c where c.id = :id")
	Aircraft findAircraftById(int id);

	@Query("select c from Aircraft c")
	Collection<Aircraft> findAllAircrafts();

	@Query("SELECT COUNT(a) > 0 FROM Aircraft a WHERE a.registrationNumber = :registrationNumber")
	boolean existsByRegistrationNumber(String registrationNumber);
}
