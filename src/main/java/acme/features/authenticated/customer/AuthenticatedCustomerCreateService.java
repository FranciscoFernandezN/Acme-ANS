
package acme.features.authenticated.customer;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.principals.UserAccount;
import acme.client.helpers.PrincipalHelper;
import acme.client.helpers.RandomHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.Customer;

@GuiService
public class AuthenticatedCustomerCreateService extends AbstractGuiService<Authenticated, Customer> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedCustomerRepository repository;

	// AbstractService<Authenticated, Provider> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findUserAccountById(userAccountId);

		object = new Customer();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Customer object) {
		assert object != null;

		super.bindObject(object, "identifier", "phoneNumber", "physicalAddress", "city", "country", "earnedPoints");

	}

	@Override
	public void validate(final Customer object) {
		assert object != null;

		Collection<Customer> customers = this.repository.findAllCustomers();
		Collection<String> customerIds = customers.stream().map(Customer::getIdentifier).toList();
		String identifier = object.getIdentifier();

		if (identifier != null && identifier.length() >= 2) {
			super.state(!customerIds.contains(object.getIdentifier()), "identifier", "authenticated.customer.create.not-unique-identifier");
			DefaultUserIdentity dui = this.repository.findUserAccountById(super.getRequest().getPrincipal().getAccountId()).getIdentity();
			boolean identifierBegin = object.getIdentifier().charAt(0) == dui.getName().charAt(0) && object.getIdentifier().charAt(1) == dui.getSurname().charAt(0);
			super.state(identifierBegin, "identifier", "authenticated.customer.create.not-initials-in-identifier");
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
		String identifierBegin;
		DefaultUserIdentity dui = this.repository.findUserAccountById(super.getRequest().getPrincipal().getAccountId()).getIdentity();
		identifierBegin = String.valueOf(dui.getName().charAt(0)) + String.valueOf(dui.getSurname().charAt(0));
		String identifier = "";

		dataset = super.unbindObject(object, "phoneNumber", "physicalAddress", "city", "country", "earnedPoints");

		if (super.getRequest().hasData("identifier"))
			identifier = super.getRequest().getData("identifier", String.class);
		else {
			boolean uniqueIdentifier = false;
			while (!uniqueIdentifier) {
				identifier += identifierBegin;
				for (int i = 0; i < 6; i++)
					identifier += RandomHelper.nextInt(10);
				uniqueIdentifier = this.repository.findCustomerByIdentifier(identifier) == null;
			}
		}

		dataset.put("identifier", identifier);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
