
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
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Customer.class));
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int customerId;
		Integer bookingId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		if (super.getRequest().hasData(MASTER_ID)) {
			bookingId = super.getRequest().getData(MASTER_ID, int.class);
			passengers = this.repository.findPassengersByBookingId(bookingId);
		} else
			passengers = this.repository.findPassengersByCustomerId(customerId);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passengers) {
		Dataset dataset;
		boolean showCreate = true;

		dataset = super.unbindObject(passengers, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraftMode");
		super.getResponse().addData(dataset);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

	@Override
	public void unbind(final Collection<Passenger> passengers) {
		int bookingId;
		Booking booking;
		boolean showCreate = true;

		if (super.getRequest().hasData(MASTER_ID)) {
			bookingId = super.getRequest().getData(MASTER_ID, int.class);
			booking = this.repository.findBookingById(bookingId);
			showCreate = booking.getIsDraftMode() && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

			super.getResponse().addGlobal("booking", bookingId);
		}

		super.getResponse().addGlobal("showCreate", showCreate);

	}

}
