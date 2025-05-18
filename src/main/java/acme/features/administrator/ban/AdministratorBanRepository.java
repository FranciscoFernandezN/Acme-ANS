
package acme.features.administrator.ban;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.BelongsTo;
import acme.entities.passengers.Ban;
import acme.entities.passengers.Passenger;

@Repository
public interface AdministratorBanRepository extends AbstractRepository {

	@Query("select b from Ban b where b.id = :id")
	Ban findBanById(int id);

	@Query("select b from Ban b where b.passenger.id = :id")
	List<Ban> findBanOfPassenger(int id);

	@Query("select p from Passenger p where p.isDraftMode = false")
	List<Passenger> findAllPassengers();

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select distinct(b.passenger) from Ban b where b.liftDate is null or b.liftDate > :today")
	Collection<Passenger> findBannedPassengers(Date today);

	@Query("select distinct(b.passenger) from Ban b where b.id != :id and (b.liftDate is null or b.liftDate > :today)")
	Collection<Passenger> findBannedPassengersExceptBan(Date today, int id);

	@Query("select distinct(b.passenger) from Ban b where b.passenger not in (select b.passenger from Ban b where b.liftDate is null or b.liftDate > :today)")
	Collection<Passenger> findLiftedBanPassengers(Date today);

	@Query("select bt from BelongsTo bt where bt.passenger.id = :id")
	Collection<BelongsTo> findBelongsToOfPassenger(int id);

	@Query("select bt.passenger from BelongsTo bt where bt.booking.id = :id")
	Collection<Passenger> findPassengersByBookingId(int id);

}
