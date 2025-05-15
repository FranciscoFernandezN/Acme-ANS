
package acme.features.administrator.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;

@Repository
public interface AdministratorBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select b from Booking b where b.isDraftMode = false")
	Collection<Booking> findPublishedBookings();

	@Query("select bt.passenger from BelongsTo bt where bt.booking.id = :id")
	Collection<Passenger> findPassengersByBookingId(int id);

}
