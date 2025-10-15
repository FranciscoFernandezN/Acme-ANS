
package acme.features.assistanceAgent.trackingLog;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackinglogs.LogState;
import acme.entities.trackinglogs.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository aatlr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		Boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		if (status && super.getRequest().hasData("claim")) {
			AssistanceAgent agent;
			agent = (AssistanceAgent) super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class);
			List<Claim> claims = this.aatlr.findAllClaimsByAgentId(agent.getId());
			int claimId = super.getRequest().getData("claim", int.class);
			Claim claim = this.aatlr.findClaimById(claimId);
			status = claimId == 0 || claim != null && claims.contains(claim);
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog = new TrackingLog();
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class);
		trackingLog.setAgent(agent);
		trackingLog.setIsPublished(false);

		Date lastUpdateMoment;
		lastUpdateMoment = MomentHelper.getCurrentMoment();
		trackingLog.setLastUpdateMoment(lastUpdateMoment);
		trackingLog.setIndicator(LogState.IN_PROGRESS);
		trackingLog.setResolutionPercentage(0.00);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolution", "claim");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.aatlr.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {

		Dataset dataset;
		List<Claim> claims;
		SelectChoices claimChoices;
		SelectChoices indicatorChoices;
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class);

		claims = this.aatlr.findAllClaimsByAgentId(agent.getId());

		claimChoices = SelectChoices.from(claims, "passengerEmail", trackingLog.getClaim());
		indicatorChoices = SelectChoices.from(LogState.class, trackingLog.getIndicator());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "indicator", "claim", "isPublished");
		dataset.put("indicator", indicatorChoices);
		dataset.put("claim", claimChoices);

		super.getResponse().addData(dataset);
	}
}
