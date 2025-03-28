
package acme.features.assistanceAgents.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimState;
import acme.entities.claims.ClaimType;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentsClaimShowService extends AbstractGuiService<AssistanceAgent, Claim> {

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
	public void unbind(final Claim claim) {
		Dataset dataset;
		SelectChoices typeChoices;
		SelectChoices indicatorChoices;

		typeChoices = SelectChoices.from(ClaimType.class, claim.getClaimType());
		indicatorChoices = SelectChoices.from(ClaimState.class, claim.getIndicator());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "claimType", "indicator", "isPublished", "leg");
		dataset.put("claimType", typeChoices);
		dataset.put("indicator", indicatorChoices);
		super.getResponse().addData(dataset);
	}
}
