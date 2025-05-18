
package acme.features.administrator.passenger;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Ban;
import acme.entities.passengers.Passenger;

@Repository
public interface AdministratorPassengerRepository extends AbstractRepository {

	@Query("select bt.passenger from BelongsTo bt where bt.booking.id = :id")
	Collection<Passenger> findPassengersByBookingId(int id);

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select bt.booking from BelongsTo bt where bt.passenger.id = :id and bt.booking.isDraftMode = false")
	Collection<Booking> findPublishedBookingsOfPassenger(int id);

	@Query("select distinct(b.passenger) from Ban b where b.liftDate is null or b.liftDate > :today")
	Collection<Passenger> findBannedPassengers(Date today);

	@Query("select distinct(b.passenger) from Ban b where b.passenger not in (select b.passenger from Ban b where b.liftDate is null or b.liftDate > :today)")
	Collection<Passenger> findLiftedBanPassengers(Date today);

	@Query("select distinct(b.passenger) from Ban b where b.banIssuedDate >= :lastMonth")
	Collection<Passenger> findPassengersBannedLastMonth(Date lastMonth);

	@Query("select b.nationality from Ban b where b.passenger.id = :id order by b.banIssuedDate")
	List<String> findNationality(int id);

	@Query("select b from Ban b where b.passenger.id = :id")
	List<Ban> findBanOfPassenger(int id);

}
