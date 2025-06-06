
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
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		int bookingId;
		Booking booking;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		boolean hasMasterId = super.getRequest().hasData(CustomerPassengerController.MASTER_ID);
		boolean hasBooking = super.getRequest().hasData("booking");
		masterId = hasMasterId ? super.getRequest().getData(CustomerPassengerController.MASTER_ID, int.class) : 0;

		if (status && hasMasterId) {
			booking = this.repository.findBookingById(masterId);
			status = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && booking.getIsDraftMode();
		}

		if (status && hasBooking) {
			bookingId = super.getRequest().getData("booking", int.class);
			booking = this.repository.findBookingById(bookingId);
			status = bookingId == 0 || booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && booking.getIsDraftMode();
			if (status && hasMasterId)
				status = bookingId == masterId;
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Passenger passenger;

		passenger = new Passenger();
		passenger.setIsDraftMode(true);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {
		Boolean passportAlreadyInUse;
		int customerId = super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId();

		passportAlreadyInUse = !this.repository.findAllPassportNumbersOfCustomer(customerId).contains(passenger.getPassportNumber());
		super.state(passportAlreadyInUse, "passportNumber", "customer.passenger.create.passport-number-must-be-unique");

		int bookingId;
		bookingId = super.getRequest().getData("booking", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		super.state(booking != null, "booking", "customer.passenger.create.booking-must-be-chosen");

		if (booking != null) {
			boolean yours = super.getRequest().getPrincipal().hasRealm(booking.getCustomer());
			super.state(yours, "booking", "customer.passenger.create.booking-not-yours");
			super.state(booking.getIsDraftMode(), "booking", "customer.passenger.create.booking-is-already-published");
		}
	}

	@Override
	public void perform(final Passenger passenger) {
		passenger.setIsDraftMode(true);

		BelongsTo belongsTo = new BelongsTo();

		belongsTo.setBooking(this.repository.findBookingById(super.getRequest().getData("booking", int.class)));
		belongsTo.setPassenger(passenger);

		this.repository.save(passenger);
		this.repository.save(belongsTo);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		int bookingId;
		SelectChoices bookingChoices = new SelectChoices();
		Collection<Booking> bookings = this.repository.findBookingByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId());

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraftMode");

		if (super.getRequest().hasData(CustomerPassengerController.MASTER_ID)) {
			bookingId = super.getRequest().getData(CustomerPassengerController.MASTER_ID, int.class);
			bookings.stream().forEach(b -> bookingChoices.add(String.valueOf(b.getId()), String.format("%s - %s", b.getLocatorCode(), b.getFlight().getTag()), bookingId == b.getId()));
			dataset.put("booking", bookingId);
			dataset.put("createdInBooking", true);
		} else {
			bookingId = super.getRequest().hasData("booking") ? super.getRequest().getData("booking", int.class) : -1;
			bookings.stream().forEach(b -> bookingChoices.add(String.valueOf(b.getId()), String.format("%s - %s", b.getLocatorCode(), b.getFlight().getTag()), bookingId == b.getId()));
			bookingChoices.add("0", "----", bookingId <= 0);
			dataset.put("booking", bookingId);
			dataset.put("createdInBooking", false);
		}

		dataset.put("bookingChoices", bookingChoices);
		dataset.put("updatedPassenger", false);

		super.getResponse().addData(dataset);
	}

}
