
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.RandomHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.BelongsTo;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().hasData("flight")) {
			int flightId = super.getRequest().getData("flight", int.class);
			Flight flight = this.repository.findFlightById(flightId);
			status = flightId == 0 || flight != null && !flight.getIsDraftMode();
		}

		if (status && super.getRequest().hasData("passenger")) {
			int passengerId = super.getRequest().getData("passenger", int.class);
			Passenger passenger = this.repository.findPassengerById(passengerId);
			status = passengerId == 0 || passenger != null && this.repository.findPassengersByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId()).contains(passenger);
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Customer customer;
		Booking booking;

		customer = (Customer) super.getRequest().getPrincipal().getRealmOfType(Customer.class);

		booking = new Booking();
		booking.setCustomer(customer);
		booking.setIsDraftMode(true);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		super.bindObject(booking, "locatorCode", "travelClass", "lastNibble");

		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		booking.setFlight(flight);

		Money auxMon = new Money();
		auxMon.setAmount(0.0);
		auxMon.setCurrency("EUR");

		booking.setPrice(auxMon);

	}

	@Override
	public void validate(final Booking booking) {
		int flightId;
		flightId = super.getRequest().getData("flight", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		if (flight != null)
			super.state(!flight.getIsDraftMode(), "flight", "customer.booking.create.flight-must-be-published");
		else {
			super.state(flightId > 0, "flight", "customer.booking.create.flight-must-be-chosen");
			super.state(flightId <= 0, "flight", "customer.booking.create.flight-does-not-exist");
		}

		super.state(this.repository.findBookingByLocatorCode(super.getRequest().getData("locatorCode", String.class)) == null, "locatorCode", "customer.booking.create.locator-not-unique");

		int passengerId;
		passengerId = super.getRequest().getData("passenger", int.class);
		Passenger passenger = this.repository.findPassengerById(passengerId);

		if (passenger != null) {
			boolean yours = this.repository.findPassengersByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId()).contains(passenger);
			super.state(yours, "passenger", "customer.booking.create.passenger-not-yours");
		} else
			super.state(passengerId <= 0, "passenger", "customer.booking.create.passenger-does-not-exist");

		super.state(booking.getTravelClass() != null, "travelClass", "customer.booking.create.travel-class-does-not-exist");

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

		if (super.getBuffer().getErrors().hasErrors()) {
			booking.setIsDraftMode(true);
			System.out.print(super.getBuffer().getErrors());
		}

		String locatorCode = "";

		if (super.getRequest().hasData("locatorCode"))
			locatorCode = super.getRequest().getData("locatorCode", String.class);
		else {
			int firstUppercaseIndex = 'A';
			boolean uniqueLocatorCode = false;

			while (!uniqueLocatorCode) {
				locatorCode = "";
				for (int i = 0; i < RandomHelper.nextInt(6, 8); i++) {
					Integer letterIndex = RandomHelper.nextInt(26);
					char letter = (char) (firstUppercaseIndex + letterIndex);
					int num = RandomHelper.nextInt(10);
					String chosen = RandomHelper.nextInt(2) == 1 ? String.valueOf(letter) : String.valueOf(num);
					locatorCode += chosen;
				}
				uniqueLocatorCode = this.repository.findBookingByLocatorCode(locatorCode) == null;
			}
		}
		SelectChoices travelClasses;
		Collection<Flight> flights = this.repository.findAllFlightsForBooking();

		travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		SelectChoices flightChoices = new SelectChoices();
		int flightId = super.getRequest().hasData("flight") ? super.getRequest().getData("flight", int.class) : -1;
		flights.stream().forEach(f -> flightChoices.add(String.valueOf(f.getId()), String.format("%s - %s - %s", f.getOrigin(), f.getDestiny(), f.getCost()), flightId == f.getId()));
		flightChoices.add("0", "----", flightId <= 0);

		Collection<Passenger> passengers = this.repository.findPassengersByCustomerId(customerId);
		passengers.removeAll(this.repository.findPassengersByBookingId(booking.getId()));
		SelectChoices passengerChoices = new SelectChoices();
		int passengerId = super.getRequest().hasData("passenger") ? super.getRequest().getData("passenger", int.class) : -1;
		passengers.stream().distinct().forEach(p -> passengerChoices.add(String.valueOf((Integer) p.getId()), String.format("%s - %s", p.getPassportNumber(), p.getFullName()), passengerId == p.getId()));
		passengerChoices.add("0", "----", passengerId <= 0);

		dataset = super.unbindObject(booking, "travelClass", "lastNibble", "isDraftMode");
		dataset.put("locatorCode", locatorCode);
		dataset.put("travelClasses", travelClasses);
		dataset.put("flight", "0");
		dataset.put("flightChoices", flightChoices);
		dataset.put("passenger", passengerId);
		dataset.put("passengerChoices", passengerChoices);
		dataset.put("updatedBooking", false);
		dataset.put("purchaseMoment", MomentHelper.getCurrentMoment());

		super.getResponse().addData(dataset);
	}

}
