
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLogs.ActivityLog;
import acme.entities.flightAssignments.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogCreate extends AbstractGuiService<FlightCrewMember, ActivityLog> {
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
		ActivityLog activityLog = new ActivityLog();
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel");

		int flightAssignmentId = super.getRequest().getData("flightAssignment", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		activityLog.setLeg(flightAssignment.getLeg());
		activityLog.setFlightCrewMember(flightAssignment.getFlightCrewMember());
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		boolean isPublishedAssignment = activityLog.getLeg().getIsDraftMode() == false;
		super.state(isPublishedAssignment, "flightAssignment", "flightCrewMember.activity-log.error.unpublished-assignment");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "typeOfIncident", "description", "severityLevel", "registrationMoment");

		super.getResponse().addData(dataset);
	}
}
