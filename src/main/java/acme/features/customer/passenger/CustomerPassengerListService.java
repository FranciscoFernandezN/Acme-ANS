
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

	final static String					MASTER_ID	= "bookingId";

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository	repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingId;
		Booking booking;
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		boolean createdInBooking = super.getRequest().hasData(CustomerPassengerController.MASTER_ID);

		if (status && createdInBooking) {
			bookingId = super.getRequest().getData(CustomerPassengerController.MASTER_ID, int.class);
			booking = this.repository.findBookingById(bookingId);
			status = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int customerId;
		Integer bookingId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		if (super.getRequest().hasData(CustomerPassengerListService.MASTER_ID)) {
			bookingId = super.getRequest().getData(CustomerPassengerListService.MASTER_ID, int.class);
			passengers = this.repository.findPassengersByBookingId(bookingId);
		} else
			passengers = this.repository.findPassengersByCustomerId(customerId);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passengers) {
		Dataset dataset;
		boolean showCreate = true;
		String specialNeeds = passengers.getSpecialNeeds();

		dataset = super.unbindObject(passengers, "fullName", "email", "passportNumber", "dateOfBirth", "isDraftMode");
		dataset.put("specialNeeds", specialNeeds.isBlank() ? "N/A" : specialNeeds);
		super.getResponse().addData(dataset);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

	@Override
	public void unbind(final Collection<Passenger> passengers) {
		int bookingId;
		Booking booking;
		boolean showCreate = true;

		if (super.getRequest().hasData(CustomerPassengerListService.MASTER_ID)) {
			bookingId = super.getRequest().getData(CustomerPassengerListService.MASTER_ID, int.class);
			booking = this.repository.findBookingById(bookingId);
			showCreate = booking.getIsDraftMode() && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

			super.getResponse().addGlobal("booking", bookingId);
		}

		super.getResponse().addGlobal("showCreate", showCreate);

	}

}
