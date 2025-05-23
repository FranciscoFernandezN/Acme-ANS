
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerShowService extends AbstractGuiService<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class) && this.repository.findPassengersByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId()).contains(passenger);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		int passengerId;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		int bookingId;
		Collection<Booking> bookings = this.repository.findBookingByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId());
		bookings.removeAll(this.repository.findBookingByPassengerId(passenger.getId()));

		SelectChoices bookingChoices = new SelectChoices();

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraftMode");

		bookingId = super.getRequest().hasData("booking") ? super.getRequest().getData("booking", int.class) : -1;
		bookings.stream().forEach(b -> bookingChoices.add(String.valueOf(b.getId()), String.format("%s - %s", b.getLocatorCode(), b.getFlight().getTag()), bookingId == b.getId()));
		bookingChoices.add("0", "----", bookingId <= 0);
		dataset.put("booking", bookingId);

		dataset.put("bookingChoices", bookingChoices);
		dataset.put("createdInBooking", false);

		super.getResponse().addData(dataset);

	}

}
