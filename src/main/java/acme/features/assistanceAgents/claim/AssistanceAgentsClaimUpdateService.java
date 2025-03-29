
package acme.features.assistanceAgents.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimState;
import acme.entities.claims.ClaimType;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentsClaimUpdateService extends AbstractGuiService<AssistanceAgent, Claim> {

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
		Claim claim;
		int claimId;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.aacr.findClaimById(claimId);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "registrationMoment", "passengerEmail", "description", "claimType", "indicator", "isPublished", "leg");

		Leg leg;
		int legId;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.aacr.findLegById(legId);

		claim.setLeg(leg);
	}

	@Override
	public void validate(final Claim claim) {
		Leg leg = this.aacr.findLegByClaimId(claim.getId());
		super.state(!claim.getIsPublished() || claim.getIsPublished() && leg != null && !leg.getIsDraftMode(), "isPublished", "assistanceAgent.claim.create.cant-be-published");
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

		dataset = super.unbindObject(claim, "passengerEmail", "description", "claimType", "indicator", "isPublished", "leg");
		dataset.put("claimType", typeChoices);
		dataset.put("indicator", indicatorChoices);
		dataset.put("leg", legChoices);

		super.getResponse().addData(dataset);
	}
}
