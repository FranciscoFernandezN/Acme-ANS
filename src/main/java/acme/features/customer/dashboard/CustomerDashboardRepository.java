
package acme.features.customer.dashboard;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.customer.id = :id AND b.isDraftMode = false")
	Collection<Booking> findAllBookings(int id);

	@Query("SELECT SUM(b.price.amount) FROM Booking b WHERE b.purchaseMoment > :lastYear AND b.customer.id = :id AND b.isDraftMode = false")
	Double moneySpentLastYear(Date lastYear, int id);

	@Query("select bt.passenger from BelongsTo bt where bt.booking.id = :id AND bt.booking.isDraftMode = false")
	Collection<Passenger> findPassengersByBookingId(int id);

}
