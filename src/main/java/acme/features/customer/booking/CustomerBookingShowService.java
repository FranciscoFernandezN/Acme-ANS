
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
import acme.entities.supportedcurrency.SupportedCurrency;
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

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status) {
			bookingId = super.getRequest().getData("id", int.class);
			booking = this.repository.findBookingById(bookingId);
			status = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());
		}

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
		int customerId = super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId();

		SelectChoices travelClasses;
		Collection<Flight> flights = this.repository.findAllFlightsForBooking();

		travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		SelectChoices flightChoices = new SelectChoices();

		flights.stream().forEach(f -> flightChoices.add(String.valueOf(f.getId()),
			String.format("%s - %s, %s - %s, %s, %s", f.getOrigin(), f.getDestiny(), f.getScheduledDeparture(), f.getScheduledArrival(), SupportedCurrency.convertToDefault(f.getCost()), f.getTag()), booking.getFlight().getId() == f.getId()));

		Collection<Passenger> passengers = this.repository.findPassengersByCustomerId(customerId);
		passengers.removeAll(this.repository.findPassengersByBookingId(booking.getId()));
		SelectChoices passengerChoices = new SelectChoices();
		passengers.stream().distinct().forEach(p -> passengerChoices.add(String.valueOf((Integer) p.getId()), String.format("%s - %s", p.getPassportNumber(), p.getFullName()), false));
		passengerChoices.add("0", "----", true);

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "isDraftMode", "price");
		dataset.put("defaultCost", SupportedCurrency.convertToDefault(booking.getPrice()));
		dataset.put("travelClasses", travelClasses);
		dataset.put("flightChoices", flightChoices);
		dataset.put("city", booking.getFlight().getDestinyAirport().getCity());
		dataset.put("passenger", -1);
		dataset.put("passengerChoices", passengerChoices);
		dataset.put("updatedBooking", true);

		super.getResponse().addData(dataset);
	}

}
