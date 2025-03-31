
package acme.features.assistanceAgents.claim;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimState;
import acme.entities.claims.ClaimType;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentsClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentsClaimRepository aacr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class));
	}

	@Override
	public void load() {
		Claim claim = new Claim();
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class);
		claim.setAgent(agent);
		claim.setIsPublished(false);

		Date registrationMoment;
		registrationMoment = MomentHelper.getCurrentMoment();
		claim.setRegistrationMoment(registrationMoment);
		claim.setIndicator(ClaimState.IN_PROGRESS);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "passengerEmail", "description", "claimType", "isPublished", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		int legId;
		legId = super.getRequest().getData("leg", int.class);

		Leg leg = this.aacr.findLegById(legId);
		super.state(!claim.getIsPublished() || claim.getIsPublished() && leg != null && !leg.getIsDraftMode(), "isPublished", "assistance-agent.claim.create.cant-be-published");
	}

	@Override
	public void perform(final Claim claim) {
		this.aacr.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		if (super.getBuffer().getErrors().hasErrors())
			claim.setIsPublished(false);

		Dataset dataset;
		SelectChoices typeChoices;
		SelectChoices indicatorChoices;
		List<Leg> legs;
		SelectChoices legChoices;
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class);

		legs = this.aacr.findAllLegsByAirlineId(agent.getAirline().getId());

		legChoices = SelectChoices.from(legs, "uniqueIdentifier", claim.getLeg());
		typeChoices = SelectChoices.from(ClaimType.class, claim.getClaimType());
		indicatorChoices = SelectChoices.from(ClaimState.class, claim.getIndicator());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "indicator", "claimType", "isPublished", "leg");
		dataset.put("claimType", typeChoices);
		dataset.put("indicator", indicatorChoices);
		dataset.put("leg", legChoices);

		super.getResponse().addData(dataset);
	}
}
