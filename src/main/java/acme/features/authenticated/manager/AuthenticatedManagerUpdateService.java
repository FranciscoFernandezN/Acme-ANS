package acme.features.authenticated.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.features.authenticated.provider.AuthenticatedProviderRepository;
import acme.realms.Manager;
import acme.realms.Provider;

@GuiService
public class AuthenticatedManagerUpdateService extends AbstractGuiService<Authenticated, Manager> {
	
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedManagerRepository mr;

	// AbstractService interface ----------------------------------------------ç


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Manager object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.mr.findManagerByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Manager object) {
		assert object != null;

		super.bindObject(object, "identifierNumber", "yearsOfExperience", "birth", "linkPicture");
		
		Airline airline;
		int airlineId;

		airlineId = super.getRequest().getData("airlineManaging", int.class);
		airline = this.mr.findAirlineById(airlineId);
		
		object.setAirlineManaging(airline);
	}

	@Override
	public void validate(final Manager object) {
		assert object != null;
		
		List<Manager> managers = this.mr.findAllManagers();
		List<String> managerIds = managers.stream().filter(m -> m.getId() != object.getId()).map(Manager::getIdentifierNumber).toList();

		if (object.getIdentifierNumber() != null)
			super.state(!managerIds.contains(object.getIdentifierNumber()), "identifierNumber", "authenticated.manager.create.not-unique-identifier");
	}

	@Override
	public void perform(final Manager object) {
		assert object != null;

		this.mr.save(object);
	}

	@Override
	public void unbind(final Manager object) {
		assert object != null;

		Dataset dataset;
		
		List<Airline> airlines;
		SelectChoices airlineChoices;
		
		airlines = this.mr.findAllAirlines();
		
		dataset = super.unbindObject(object, "identifierNumber", "yearsOfExperience", "birth", "linkPicture");
		
		airlineChoices = SelectChoices.from(airlines, "iATACode", object.getAirlineManaging());
		dataset.put("airlineIATACodes", airlineChoices);
		
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
	
}
