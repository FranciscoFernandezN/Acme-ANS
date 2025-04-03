
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class) && super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId() == booking.getCustomer().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int bookingId;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;

		if (super.getBuffer().getErrors().hasErrors()) {
			booking.setIsDraftMode(true);
			System.out.print(super.getBuffer().getErrors());
		}

		SelectChoices travelClasses;
		Collection<Flight> flights = this.repository.findAllFlightsForBooking();

		travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		SelectChoices flightChoices = new SelectChoices();
		flights.stream().forEach(f -> flightChoices.add(String.valueOf(f.getId()), String.format("%s - %s - %s", f.getOrigin(), f.getDestiny(), f.getCost()), booking.getFlight().getId() == f.getId()));

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "isDraftMode");
		dataset.put("travelClasses", travelClasses);
		dataset.put("flightChoices", flightChoices);
		dataset.put("passportNumber", booking.getPassenger().getPassportNumber());

		super.getResponse().addData(dataset);
	}

}
