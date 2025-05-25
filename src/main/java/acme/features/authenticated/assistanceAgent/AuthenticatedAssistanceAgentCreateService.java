
package acme.features.authenticated.assistanceAgent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.realms.AssistanceAgent;

@GuiService
public class AuthenticatedAssistanceAgentCreateService extends AbstractGuiService<Authenticated, AssistanceAgent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedAssistanceAgentRepository aar;

	// AbstractService<Authenticated, Provider> ---------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AssistanceAgent object;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.aar.findUserAccountById(userAccountId);

		object = new AssistanceAgent();
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final AssistanceAgent object) {

		super.bindObject(object, "employeeCode", "languages", "firstWorkingDate", "biography", "salary", "photoLink");

		Airline airline;
		int airlineId;

		airlineId = super.getRequest().getData("airline", int.class);
		airline = this.aar.findAirlineById(airlineId);

		object.setAirline(airline);

	}

	@Override
	public void validate(final AssistanceAgent object) {

		List<AssistanceAgent> AssistanceAgents = this.aar.findAllAssistanceAgents();
		List<String> AssistanceAgentIds = AssistanceAgents.stream().map(AssistanceAgent::getEmployeeCode).toList();

		if (object.getEmployeeCode() != null)
			super.state(!AssistanceAgentIds.contains(object.getEmployeeCode()), "employeeCode", "authenticated.assistance-agent.create.not-unique-identifier");
	}

	@Override
	public void perform(final AssistanceAgent object) {

		this.aar.save(object);
	}

	@Override
	public void unbind(final AssistanceAgent object) {

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
