
package acme.features.assistanceAgents.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentsClaimInProgressListService extends AbstractGuiService<AssistanceAgent, Claim> {

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
		List<Claim> claims;
		int agentId;

		agentId = super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class).getId();
		claims = this.aacr.findAllInProgressClaimsByAgentId(agentId);

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "claimType", "indicator", "isPublished", "leg");
		dataset.put("leg", claim.getLeg().getUniqueIdentifier());
		super.getResponse().addData(dataset);
	}
}
