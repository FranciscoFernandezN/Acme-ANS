
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.BelongsTo;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerUpdateService extends AbstractGuiService<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int passengerId = 0;
		Passenger passenger = null;
		boolean hasBooking = false;
		int bookingId;
		Booking booking;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().hasData("id")) {
			passengerId = super.getRequest().getData("id", int.class);
			passenger = this.repository.findPassengerById(passengerId);
			hasBooking = super.getRequest().hasData("booking");
		} else
			status = false;

		status = status && this.repository.findPassengersByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId()).contains(passenger) && passenger.getIsDraftMode();

		if (status && hasBooking) {
			bookingId = super.getRequest().getData("booking", int.class);
			booking = this.repository.findBookingById(bookingId);
			status = bookingId == 0 || booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && !this.repository.findBookingByPassengerId(passengerId).contains(booking) && booking.getIsDraftMode();
		}

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
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {
		Boolean passportAlreadyInUse;
		Passenger oldPassenger;
		int customerId = super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId();

		oldPassenger = this.repository.findPassengerById(passenger.getId());
		passportAlreadyInUse = !this.repository.findAllPassportNumbersOfCustomer(customerId).contains(passenger.getPassportNumber()) || oldPassenger.getPassportNumber().equals(passenger.getPassportNumber());

		super.state(passportAlreadyInUse, "passportNumber", "customer.passenger.update.passport-number-must-be-unique");

		int bookingId;
		bookingId = super.getRequest().getData("booking", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		if (booking != null) {
			boolean yours = super.getRequest().getPrincipal().hasRealm(booking.getCustomer());
			super.state(yours, "booking", "customer.passenger.update.booking-not-yours");
			if (yours)
				super.state(booking.getIsDraftMode(), "booking", "customer.passenger.update.booking-is-already-published");
		}

	}

	@Override
	public void perform(final Passenger passenger) {
		passenger.setIsDraftMode(true);
		this.repository.save(passenger);

		if (this.repository.findBookingById(super.getRequest().getData("booking", int.class)) != null) {
			BelongsTo belongsTo = new BelongsTo();

			belongsTo.setBooking(this.repository.findBookingById(super.getRequest().getData("booking", int.class)));
			belongsTo.setPassenger(passenger);
			this.repository.save(belongsTo);
		}

	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		int bookingId;
		Collection<Booking> bookings = this.repository.findBookingByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId());
		bookings.removeAll(this.repository.findBookingByPassengerId(passenger.getId()));

		SelectChoices bookingChoices = new SelectChoices();

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraftMode");

		bookingId = super.getRequest().getData("booking", int.class);
		bookings.stream().forEach(b -> bookingChoices.add(String.valueOf(b.getId()), String.format("%s - %s", b.getLocatorCode(), b.getFlight().getTag()), bookingId == b.getId()));
		bookingChoices.add("0", "----", bookingId <= 0);
		dataset.put("booking", bookingId);

		dataset.put("bookingChoices", bookingChoices);
		dataset.put("createdInBooking", false);
		dataset.put("updatedPassenger", true);

		super.getResponse().addData(dataset);
	}

}
