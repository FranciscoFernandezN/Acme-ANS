
package acme.features.authenticated.customer;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.DefaultUserIdentity;
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
		String identifier = customer.getIdentifier();

		if (identifier != null && !oldCustomer.getIdentifier().equals(identifier) && identifier.length() >= 2) {
			super.state(!customerIds.contains(customer.getIdentifier()), "identifier", "authenticated.customer.update.not-unique-identifier");
			DefaultUserIdentity dui = this.repository.findUserAccountById(super.getRequest().getPrincipal().getAccountId()).getIdentity();
			boolean identifierBegin = customer.getIdentifier().charAt(0) == dui.getName().charAt(0) && customer.getIdentifier().charAt(1) == dui.getSurname().charAt(0);
			super.state(identifierBegin, "identifier", "authenticated.customer.update.not-initials-in-identifier");
		}
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
