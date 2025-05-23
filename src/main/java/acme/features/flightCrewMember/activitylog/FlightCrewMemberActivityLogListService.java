
package acme.features.flightCrewMember.activitylog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylogs.ActivityLog;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean authorised = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int userId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<ActivityLog> activityLogs = this.repository.findAllActivityLogsByFlightCrewMemberId(userId);

		super.getBuffer().addData(activityLogs);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");

		dataset.put("isDraftMode", activityLog.getIsDraftMode() ? "✓" : "x");

		if (activityLog.getFlightAssignment() != null && activityLog.getFlightAssignment().getLeg() != null)
			dataset.put("flightNumber", activityLog.getFlightAssignment().getLeg().getFlightNumber());

		super.getResponse().addData(dataset);
	}
}
