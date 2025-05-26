
package acme.features.assistanceAgent.trackingLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackinglogs.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogUpdateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository aatlr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		TrackingLog trackingLog;
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class);

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.aatlr.findTrackingLogById(trackingLogId);

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && agent.getId() == trackingLog.getAgent().getId() && !trackingLog.getIsPublished() && trackingLog != null;
		if (status && super.getRequest().hasData("claim")) {
			List<Claim> claims = this.aatlr.findAllClaimsByAgentId(agent.getId());
			int claimId = super.getRequest().getData("claim", int.class);
			Claim claim = this.aatlr.findClaimById(claimId);
			status = claimId == 0 || claim != null && claims.contains(claim);
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int trackingLogId;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.aatlr.findTrackingLogById(trackingLogId);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "resolution", "claim", "isPublished");

		Claim claim;
		int claimId;

		claimId = super.getRequest().getData("claim", int.class);
		claim = this.aatlr.findClaimById(claimId);

		trackingLog.setClaim(claim);
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		int claimId;
		Claim claim;
		claimId = super.getRequest().getData("claim", int.class);
		claim = this.aatlr.findClaimById(claimId);

		if (trackingLog.getIsPublished() && claim != null)
			super.state(claim.getIsPublished(), "*", "assistance-agent.tracking-log.create.cant-be-published");

	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.aatlr.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		if (super.getBuffer().getErrors().hasErrors())
			trackingLog.setIsPublished(false);

		Dataset dataset;
		List<Claim> claims;
		SelectChoices claimChoices;
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class);

		claims = this.aatlr.findAllClaimsByAgentId(agent.getId());

		claimChoices = SelectChoices.from(claims, "passengerEmail", trackingLog.getClaim());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "claim", "isPublished");
		dataset.put("claim", claimChoices);

		super.getResponse().addData(dataset);
	}
}
