
package acme.features.customer.dashboard;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.datatypes.Money;
import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.customer.id = :id")
	Collection<Booking> findAllBookings(int id);

	@Query("SELECT b.price FROM Booking b WHERE b.purchaseMoment > :lastYear AND b.customer.id = :id")
	Money moneySpentLastYear(Date lastYear, int id);

}
