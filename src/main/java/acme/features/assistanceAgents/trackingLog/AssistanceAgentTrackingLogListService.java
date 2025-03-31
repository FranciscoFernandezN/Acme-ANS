
package acme.features.assistanceAgents.trackingLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

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
		List<TrackingLog> trackingLog;
		int agentId;

		agentId = super.getRequest().getPrincipal().getRealmOfType(AssistanceAgent.class).getId();
		trackingLog = this.aatlr.findAllTrackingLogsByAgentId(agentId);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "claim", "isPublished");
		dataset.put("claim", trackingLog.getClaim().getId());
		super.getResponse().addData(dataset);
	}
}
