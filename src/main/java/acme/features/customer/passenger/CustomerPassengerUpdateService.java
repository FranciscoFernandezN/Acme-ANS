
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
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
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class) && super.getRequest().getPrincipal().getRealmOfType(Customer.class).getId() == passenger.getCustomer().getId();

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
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraftMode");
	}

	@Override
	public void validate(final Passenger passenger) {
		Boolean passportAlreadyInUse;
		Passenger oldPassenger;

		oldPassenger = this.repository.findPassengerById(passenger.getId());
		passportAlreadyInUse = !this.repository.findAllPassportNumbers().contains(passenger.getPassportNumber()) || oldPassenger.getPassportNumber().equals(passenger.getPassportNumber());

		super.state(passportAlreadyInUse, "passportNumber", "customer.passenger.update.passport-number-must-be-unique");
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
