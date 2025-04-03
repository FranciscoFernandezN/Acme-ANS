
package acme.features.authenticated.customer;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.Customer;

@GuiService
public class AuthenticatedCustomerUpdateService extends AbstractGuiService<Authenticated, Customer> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedCustomerRepository repository;

	// AbstractService<Authenticated, Provider> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer customer;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		customer = this.repository.findCustomerByUserAccountId(userAccountId);

		super.getBuffer().addData(customer);
	}

	@Override
	public void bind(final Customer object) {
		assert object != null;

		super.bindObject(object, "identifier", "phoneNumber", "physicalAddress", "city", "country", "earnedPoints");

	}

	@Override
	public void validate(final Customer customer) {
		assert customer != null;

		Collection<Customer> customers = this.repository.findAllCustomers();
		Collection<String> customerIds = customers.stream().map(Customer::getIdentifier).toList();
		Customer oldCustomer = this.repository.findCustomerById(customer.getId());

		if (customer.getIdentifier() != null && !oldCustomer.getIdentifier().equals(customer.getIdentifier()))
			super.state(!customerIds.contains(customer.getIdentifier()), "identifier", "authenticated.customer.create.not-unique-identifier");
	}

	@Override
	public void perform(final Customer object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Customer object) {
		assert object != null;

		Dataset dataset;
		dataset = super.unbindObject(object, "identifier", "phoneNumber", "physicalAddress", "city", "country", "earnedPoints");

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
