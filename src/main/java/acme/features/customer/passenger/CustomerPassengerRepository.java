
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.passengers.Passenger;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select p from Passenger p where p.customer.id = :id")
	Collection<Passenger> findPassengerByCustomerId(int id);

	@Query("select p.passportNumber from Passenger p")
	Collection<String> findAllPassportNumbers();

}
