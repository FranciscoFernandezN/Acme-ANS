
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
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
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Customer.class));
	}

	@Override
	public void load() {
		Customer customer;
		Passenger passenger;

		customer = (Customer) super.getRequest().getPrincipal().getRealmOfType(Customer.class);

		passenger = new Passenger();
		passenger.setCustomer(customer);
		passenger.setIsDraftMode(true);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraftMode");
	}

	@Override
	public void validate(final Passenger passenger) {
		Boolean passportAlreadyInUse;

		passportAlreadyInUse = !this.repository.findAllPassportNumbers().contains(passenger.getPassportNumber());

		super.state(passportAlreadyInUse, "passportNumber", "customer.passenger.create.passport-number-must-be-unique");
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		if (super.getBuffer().getErrors().hasErrors()) {
			passenger.setIsDraftMode(true);
			System.out.print(super.getBuffer().getErrors());
		}

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraftMode");

		super.getResponse().addData(dataset);
	}

}
