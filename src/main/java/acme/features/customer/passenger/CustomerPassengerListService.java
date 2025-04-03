
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

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

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		passengers = this.repository.findPassengerByCustomerId(customerId);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passengers) {
		Dataset dataset;

		dataset = super.unbindObject(passengers, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraftMode");

		super.getResponse().addData(dataset);
	}

}
