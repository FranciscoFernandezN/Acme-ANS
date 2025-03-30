
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
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

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
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "travelClass", "lastNibble", "isDraftMode");

		Flight flight;
		int flightId;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		booking.setFlight(flight);

		String passportNumber = super.getRequest().getData("passportNumber", String.class);

		booking.setPassenger(this.repository.findPassengerByPassportNumber(passportNumber));
	}

	@Override
	public void validate(final Booking booking) {
		String lastNibble;
		String passportNumber;
		Integer flightId;

		lastNibble = super.getRequest().getData("lastNibble", String.class);

		passportNumber = super.getRequest().getData("passportNumber", String.class);
		Passenger passenger = this.repository.findPassengerByPassportNumber(passportNumber);

		flightId = super.getRequest().getData("flight", int.class);
		Flight flight = this.repository.findFlightById(flightId);

		super.state(flight != null, "flight", "customer.booking.update.flight-does-not-exist");

		super.state(passenger != null, "passportNumber", "customer.booking.update.passenger-does-not-exist");

		super.state(booking.getIsDraftMode() || !lastNibble.isBlank(), "isDraftMode", "customer.booking.update.cant-be-published");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setPrice(booking.getFlight().getCost());

		this.repository.save(booking);
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
