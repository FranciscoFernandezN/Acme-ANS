
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.flights.Flight;
import acme.entities.passengers.Passenger;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Optional<Booking> findBookingByLocatorCode(String locatorCode);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingByCustomerId(int customerId);

	@Query("select f from Flight f where f.isDraftMode = false")
	Collection<Flight> findAllFlightsForBooking();

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select p from Passenger p where p.passportNumber = :passportNumber")
	Passenger findPassengerByPassportNumber(String passportNumber);

}
