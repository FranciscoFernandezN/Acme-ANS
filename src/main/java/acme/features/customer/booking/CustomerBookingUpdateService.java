
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.BelongsTo;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.entities.passengers.Passenger;
import acme.entities.supportedcurrency.SupportedCurrency;
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
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class) && booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && booking.getIsDraftMode();

		if (status && super.getRequest().hasData("flight")) {
			int flightId = super.getRequest().getData("flight", int.class);
			Flight flight = this.repository.findFlightById(flightId);
			status = flightId == 0 || flight != null && !flight.getIsDraftMode();
		}

		if (status && super.getRequest().hasData("passenger")) {
			int passengerId = super.getRequest().getData("passenger", int.class);
			Passenger passenger = this.repository.findPassengerById(passengerId);
			Collection<Passenger> passengersOfCustomer = this.repository.findPassengersByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId());
			boolean yours = passengersOfCustomer.contains(passenger);
			boolean passengersAreAlready = super.getRequest().hasData("id") && this.repository.findPassengersByBookingId(super.getRequest().getData("id", int.class)).remove(passenger);
			status = passengerId == 0 || yours && !passengersAreAlready;
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
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "travelClass", "lastNibble");

		Flight flight;
		int flightId;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		booking.setFlight(flight);
	}

	@Override
	public void validate(final Booking booking) {
		Integer flightId;
		int bookingId = booking.getId();

		flightId = super.getRequest().getData("flight", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		if (flight != null)
			super.state(!flight.getIsDraftMode(), "flight", "customer.booking.update.flight-must-be-published");
		else {
			super.state(flightId > 0, "flight", "customer.booking.update.flight-must-be-chosen");
			super.state(flightId <= 0, "flight", "customer.booking.update.flight-does-not-exist");
		}

		Booking bookingOfLocatorCode = this.repository.findBookingByLocatorCode(super.getRequest().getData("locatorCode", String.class));
		super.state(bookingOfLocatorCode == null || bookingOfLocatorCode.getId() == bookingId, "locatorCode", "customer.booking.update.locator-not-unique");

		int passengerId;
		passengerId = super.getRequest().getData("passenger", int.class);
		Passenger passenger = this.repository.findPassengerById(passengerId);

		if (passenger != null) {
			Collection<Passenger> passengersOfCustomer = this.repository.findPassengersByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId());
			boolean yours = passengersOfCustomer.contains(passenger);
			super.state(yours, "passenger", "customer.booking.update.passenger-not-yours");
			boolean passengersAreAlready = this.repository.findPassengersByBookingId(bookingId).remove(passenger);
			super.state(!passengersAreAlready, "passenger", "customer.booking.update.repeated-passenger");
		} else
			super.state(passengerId <= 0, "passenger", "customer.booking.publish.passenger-does-not-exist");

		super.state(booking.getTravelClass() != null, "travelClass", "customer.booking.update.travel-class-does-not-exist");

		super.state(flight != null, "flight", "customer.booking.update.flight-does-not-exist");

	}

	@Override
	public void perform(final Booking booking) {
		booking.setIsDraftMode(true);
		booking.setPrice(booking.getFlight().getCost());
		this.repository.save(booking);

		if (super.getRequest().hasData("passenger") && this.repository.findPassengerById(super.getRequest().getData("passenger", int.class)) != null) {
			BelongsTo belongsTo = new BelongsTo();

			belongsTo.setBooking(booking);
			belongsTo.setPassenger(this.repository.findPassengerById(super.getRequest().getData("passenger", int.class)));
			this.repository.save(belongsTo);
		}
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		int customerId = super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId();

		SelectChoices travelClasses;
		Collection<Flight> flights = this.repository.findAllFlightsForBooking();

		travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		SelectChoices flightChoices = new SelectChoices();
		int flightId = super.getRequest().hasData("flight") ? super.getRequest().getData("flight", int.class) : -1;

		if (flights.stream().anyMatch(f -> f.getId() == flightId))
			flights.stream().forEach(f -> flightChoices.add(String.valueOf(f.getId()),
				String.format("%s - %s, %s - %s, %s, %s", f.getOrigin(), f.getDestiny(), f.getScheduledDeparture(), f.getScheduledArrival(), SupportedCurrency.convertToDefault(f.getCost()), f.getTag()), flightId == f.getId()));
		else
			flights.stream().forEach(f -> flightChoices.add(String.valueOf(f.getId()),
				String.format("%s - %s, %s - %s, %s, %s", f.getOrigin(), f.getDestiny(), f.getScheduledDeparture(), f.getScheduledArrival(), SupportedCurrency.convertToDefault(f.getCost()), f.getTag()), booking.getFlight().getId() == f.getId()));

		Collection<Passenger> passengers = this.repository.findPassengersByCustomerId(customerId);
		passengers.removeAll(this.repository.findPassengersByBookingId(booking.getId()));
		SelectChoices passengerChoices = new SelectChoices();
		int passengerId = super.getRequest().hasData("passenger") ? super.getRequest().getData("passenger", int.class) : -1;
		passengers.stream().distinct().forEach(p -> passengerChoices.add(String.valueOf((Integer) p.getId()), String.format("%s - %s", p.getPassportNumber(), p.getFullName()), passengerId == p.getId()));
		passengerChoices.add("0", "----", passengerId <= 0);

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "isDraftMode");
		dataset.put("travelClasses", travelClasses);
		dataset.put("flightChoices", flightChoices);
		dataset.put("city", booking.getFlight().getDestinyAirport().getCity());
		dataset.put("passenger", passengerId);
		dataset.put("passengerChoices", passengerChoices);
		dataset.put("updatedBooking", true);

		super.getResponse().addData(dataset);
	}

}
