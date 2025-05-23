
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
public class CustomerPassengerPublishService extends AbstractGuiService<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		boolean hasMasterId;
		boolean hasBooking;
		int masterId;
		int bookingId;
		int passengerId;
		Booking booking;
		Passenger passenger;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class) && (passenger == null || this.repository.findPassengersByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId()).contains(passenger));

		hasMasterId = super.getRequest().hasData(CustomerPassengerController.MASTER_ID);
		hasBooking = super.getRequest().hasData("booking");
		masterId = hasMasterId ? super.getRequest().getData(CustomerPassengerController.MASTER_ID, int.class) : 0;

		if (status && hasMasterId) {
			booking = this.repository.findBookingById(masterId);
			status = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && booking.getIsDraftMode();
		}

		if (status && hasBooking) {
			bookingId = super.getRequest().getData("booking", int.class);
			booking = this.repository.findBookingById(bookingId);
			status = bookingId == 0 || booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && !this.repository.findBookingByPassengerId(passengerId).contains(booking) && booking.getIsDraftMode();
			if (status && hasMasterId)
				status = bookingId == masterId;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		int id;

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);

		if (passenger == null) {
			passenger = new Passenger();
			passenger.setIsDraftMode(true);
		}

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
		if (oldPassenger == null) {
			passportAlreadyInUse = !this.repository.findAllPassportNumbersOfCustomer(customerId).contains(passenger.getPassportNumber());
			super.state(passportAlreadyInUse, "passportNumber", "customer.passenger.create.passport-number-must-be-unique");
		} else {
			passportAlreadyInUse = !this.repository.findAllPassportNumbersOfCustomer(customerId).contains(passenger.getPassportNumber()) || oldPassenger.getPassportNumber().equals(passenger.getPassportNumber());
			super.state(passportAlreadyInUse, "passportNumber", "customer.passenger.update.passport-number-must-be-unique");
		}

		boolean passengerInBooking = !this.repository.findBookingByPassengerId(passenger.getId()).isEmpty();

		int bookingId;
		bookingId = super.getRequest().getData("booking", int.class);
		Booking booking = this.repository.findBookingById(bookingId);
		super.state(passengerInBooking || booking != null, "booking", "customer.passenger.publish.booking-must-be-chosen");

		if (booking != null) {
			boolean yours = super.getRequest().getPrincipal().hasRealm(booking.getCustomer());
			super.state(yours, "booking", "customer.passenger.publish.booking-not-yours");
			if (yours) {
				super.state(booking.getIsDraftMode(), "booking", "customer.passenger.publish.booking-is-already-published");
				if (oldPassenger != null)
					super.state(!this.repository.findBookingByPassengerId(passenger.getId()).contains(booking), "booking", "customer.passenger.publish.booking-is-repeated");
			}
		} else
			super.state(bookingId <= 0, "booking", "customer.passenger.publish.booking-does-not-exist");

	}

	@Override
	public void perform(final Passenger passenger) {
		passenger.setIsDraftMode(false);
		this.repository.save(passenger);

		if (super.getRequest().hasData("booking") && this.repository.findBookingById(super.getRequest().getData("booking", int.class)) != null) {
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
		SelectChoices bookingChoices = new SelectChoices();
		Collection<Booking> bookings = this.repository.findBookingByCustomerId(super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId());
		Boolean updatedPassenger = bookings.removeAll(this.repository.findBookingByPassengerId(passenger.getId()));

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
		dataset.put("updatedPassenger", updatedPassenger);

		super.getResponse().addData(dataset);
	}

}
