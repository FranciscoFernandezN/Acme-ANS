
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select b from Booking b where (b.customer.id = :customerId and b.isDraftMode = true)")
	Collection<Booking> findBookingByCustomerId(int customerId);

	@Query("select bt.booking from BelongsTo bt where bt.passenger.id = :id")
	Collection<Booking> findBookingByPassengerId(int id);

	@Query("select bt.passenger from BelongsTo bt where bt.booking.id = :id")
	Collection<Passenger> findPassengersByBookingId(int id);

	@Query("select bt.booking.customer from BelongsTo bt where bt.passenger.id = :id")
	Customer findCustomerByPassengerId(int id);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select bt.passenger from BelongsTo bt where bt.booking.customer.id = :id")
	Collection<Passenger> findPassengersByCustomerId(int id);

	@Query("select bt.passenger.passportNumber from BelongsTo bt where bt.booking.customer.id = :id")
	Collection<String> findAllPassportNumbersOfCustomer(int id);

}
