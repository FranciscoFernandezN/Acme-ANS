
package acme.features.administrator.aircraft;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;

@Repository
public interface AdministratorAircraftRepository extends AbstractRepository {

	@Query("SELECT a FROM Aircraft a")
	List<Aircraft> findAllAircrafts();

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Optional<Aircraft> findAircraftById(int id);

	@Query("SELECT COUNT(a) > 0 FROM Aircraft a WHERE a.registrationNumber = :registrationNumber")
	Boolean existsByRegistrationNumber(String registrationNumber);
}
