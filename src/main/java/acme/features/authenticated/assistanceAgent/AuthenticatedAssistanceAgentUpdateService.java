
package acme.features.authenticated.assistanceAgent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.realms.AssistanceAgent;

@GuiService
public class AuthenticatedAssistanceAgentUpdateService extends AbstractGuiService<Authenticated, AssistanceAgent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedAssistanceAgentRepository aar;

	// AbstractService interface ----------------------------------------------ç


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AssistanceAgent object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.aar.findAssistanceAgentByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final AssistanceAgent object) {
		assert object != null;

		super.bindObject(object, "employeeCode", "languages", "firstWorkingDate", "biography", "salary", "photoLink");

		Airline airline;
		int airlineId;

		airlineId = super.getRequest().getData("airline", int.class);
		airline = this.aar.findAirlineById(airlineId);

		object.setAirline(airline);
	}

	@Override
	public void validate(final AssistanceAgent object) {
		assert object != null;

		List<AssistanceAgent> AssistanceAgents = this.aar.findAllAssistanceAgents();
		List<String> AssistanceAgentIds = AssistanceAgents.stream().filter(m -> m.getId() != object.getId()).map(AssistanceAgent::getEmployeeCode).toList();

		if (object.getEmployeeCode() != null)
			super.state(!AssistanceAgentIds.contains(object.getEmployeeCode()), "identifierNumber", "authenticated.assistance-agent.create.not-unique-identifier");
	}

	@Override
	public void perform(final AssistanceAgent object) {
		assert object != null;

		this.aar.save(object);
	}

	@Override
	public void unbind(final AssistanceAgent object) {
		assert object != null;

		Dataset dataset;

		List<Airline> airlines;
		SelectChoices airlineChoices;

		airlines = this.aar.findAllAirlines();

		dataset = super.unbindObject(object, "employeeCode", "languages", "firstWorkingDate", "biography", "salary", "photoLink");

		airlineChoices = SelectChoices.from(airlines, "iATACode", object.getAirline());
		dataset.put("airlineIATACodes", airlineChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
