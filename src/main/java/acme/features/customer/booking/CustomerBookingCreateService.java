
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
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Customer.class));
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

		super.bindObject(booking, "locatorCode", "travelClass", "lastNibble", "isDraftMode");

		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		booking.setFlight(flight);

		Money auxMon = new Money();
		auxMon.setAmount(0.0);
		auxMon.setCurrency("EUR");

		booking.setPrice(auxMon);

		String passportNumber = super.getRequest().getData("passportNumber", String.class);

		booking.setPassenger(this.repository.findPassengerByPassportNumber(passportNumber));

	}

	@Override
	public void validate(final Booking booking) {
		String lastNibble;
		String passportNumber;

		int flightId;
		flightId = super.getRequest().getData("flight", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		super.state(flight != null, "flight", "customer.booking.create.flight-does-not-exist");

		lastNibble = super.getRequest().getData("lastNibble", String.class);

		passportNumber = super.getRequest().getData("passportNumber", String.class);
		Passenger passenger = this.repository.findPassengerByPassportNumber(passportNumber);

		super.state(booking.getTravelClass() != null, "travelClass", "customer.booking.create.travel-class-does-not-exist");

		super.state(passenger != null, "passportNumber", "customer.booking.create.passenger-does-not-exist");

		super.state(booking.getIsDraftMode() || !lastNibble.isBlank(), "isDraftMode", "customer.booking.create.cant-be-published");

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

		int firstUppercaseIndex = 'A';
		String locatorCode = "";
		boolean uniqueLocatorCode = false;

		while (!uniqueLocatorCode) {
			for (int i = 0; i < RandomHelper.nextInt(6, 8); i++) {
				Integer letterIndex = RandomHelper.nextInt(26);
				locatorCode += (char) (firstUppercaseIndex + letterIndex);
			}
			uniqueLocatorCode = this.repository.findBookingByLocatorCode(locatorCode).isEmpty();
		}
		SelectChoices travelClasses;
		Collection<Flight> flights = this.repository.findAllFlightsForBooking();

		travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		SelectChoices flightChoices = new SelectChoices();
		flights.stream().forEach(f -> flightChoices.add(String.valueOf(f.getId()), String.format("%s - %s - %s", f.getOrigin(), f.getDestiny(), f.getCost()), false));
		flightChoices.add("0", "----", true);

		dataset = super.unbindObject(booking, "travelClass", "lastNibble", "isDraftMode");
		dataset.put("locatorCode", locatorCode);
		dataset.put("travelClasses", travelClasses);
		dataset.put("flight", "0");
		dataset.put("flightChoices", flightChoices);
		dataset.put("passportNumber", "");

		super.getResponse().addData(dataset);
	}

}
