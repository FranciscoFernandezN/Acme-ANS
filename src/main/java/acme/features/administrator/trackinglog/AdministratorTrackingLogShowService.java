
package acme.features.administrator.trackinglog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackinglogs.TrackingLog;

@GuiService
public class AdministratorTrackingLogShowService extends AbstractGuiService<Administrator, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorTrackingLogRepository atlr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		TrackingLog trackingLog;
		int claimId;

		claimId = super.getRequest().getData("masterId", int.class);
		trackingLog = this.atlr.findTrackingLogByClaimId(claimId);

		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && trackingLog != null);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int claimId;

		claimId = super.getRequest().getData("masterId", int.class);
		trackingLog = this.atlr.findTrackingLogByClaimId(claimId);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "resolution", "isPublished");

		super.getResponse().addData(dataset);
	}
}
