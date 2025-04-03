
package acme.features.administrator.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;

@GuiService
public class AdministratorBookingListService extends AbstractGuiService<Administrator, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Collection<Booking> bookings;

		bookings = this.repository.findPublishedBookings();

		super.getBuffer().addData(bookings);
	}

	@Override
	public void unbind(final Booking bookings) {
		Dataset dataset;
		Passenger passenger = bookings.getPassenger();

		dataset = super.unbindObject(bookings, "locatorCode", "purchaseMoment", "travelClass", "price");

		dataset.put("fullName", passenger.getFullName());

		dataset.put("email", passenger.getEmail());

		super.getResponse().addData(dataset);
	}

}
