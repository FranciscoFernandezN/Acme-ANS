
package acme.features.assistanceAgents.trackingLog;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogDeleteService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository aatlr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class));
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int trackingLogId;
		Date lastUpdateMoment;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.aatlr.findTrackingLogById(trackingLogId);
		lastUpdateMoment = MomentHelper.getCurrentMoment();
		trackingLog.setLastUpdateMoment(lastUpdateMoment);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "resolution", "claim", "isPublished");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		super.state(!trackingLog.getIsPublished(), "isPublished", "assistance-agent.tracking-log.delete.is-published");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.aatlr.delete(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {

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
