
package acme.features.customer.booking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.supportedcurrency.SupportedCurrency;
import acme.realms.Customer;

@GuiService
public class CustomerBookingListService extends AbstractGuiService<Customer, Booking> {

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
		Collection<Booking> bookings;
		int customerId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		bookings = this.repository.findBookingByCustomerId(customerId);

		super.getBuffer().addData(bookings);
	}

	@Override
	public void unbind(final Booking bookings) {
		Dataset dataset;
		List<String> passengers = new ArrayList<>();
		this.repository.findPassengersByBookingId(bookings.getId()).stream().forEach(e -> passengers.add(e.getFullName()));

		dataset = super.unbindObject(bookings, "locatorCode", "purchaseMoment", "travelClass", "price");

		dataset.put("isDraftMode", bookings.getIsDraftMode() ? "✓" : "x");

		dataset.put("defaultPrice", SupportedCurrency.convertToDefault(bookings.getPrice()));

		dataset.put("passengers", passengers.isEmpty() ? "N/A" : passengers);

		super.getResponse().addData(dataset);
	}

}
