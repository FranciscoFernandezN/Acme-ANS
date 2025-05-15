
package acme.features.administrator.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;

public interface AdministratorPassengerRepository extends AbstractRepository {

	@Query("select bt.passenger from BelongsTo bt where bt.booking.id = :id")
	Collection<Passenger> findPassengersByBookingId(int id);

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select bt.booking from BelongsTo bt where bt.passenger.id = :id and bt.booking.isDraftMode = false")
	Collection<Booking> findPublishedBookingsOfPassenger(int id);

}
