
package acme.features.flightCrewMember.activitylog;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylogs.ActivityLog;
import acme.entities.flightassignments.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogDeleteService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

		boolean authorised = false;

		if (activityLog != null && activityLog.getFlightAssignment() != null) {
			FlightAssignment assignment = activityLog.getFlightAssignment();
			int userId = super.getRequest().getPrincipal().getActiveRealm().getId();

			boolean isOwner = assignment.getFlightCrewMember().getId() == userId;
			boolean isDraft = Boolean.TRUE.equals(activityLog.getIsDraftMode());

			Leg leg = assignment.getLeg();
			Date now = MomentHelper.getCurrentMoment();
			boolean legHasOccurred = leg != null && leg.getScheduledDeparture() != null && leg.getScheduledDeparture().before(now);

			authorised = isOwner && isDraft && legHasOccurred;
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		// No hay que modificar relaciones en un delete. Solo bind de campos relevantes si los hubiera.
		super.bindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.delete(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<FlightAssignment> assignments = this.repository.findFlightAssignmentsByFlightCrewMember(memberId);

		SelectChoices choices = new SelectChoices();
		choices.add("0", "----", activityLog.getFlightAssignment() == null);

		for (FlightAssignment a : assignments) {
			String key = String.valueOf(a.getId());
			String label = String.format("%s - %s - %s - %s", a.getLastUpDate(), a.getDuty(), a.getCurrentStatus(), a.getLeg().getFlightNumber());
			boolean selected = a.equals(activityLog.getFlightAssignment());
			choices.add(key, label, selected);
		}

		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "isDraftMode");
		dataset.put("assignments", choices);

		if (activityLog.getFlightAssignment() != null)
			dataset.put("flightAssignment", choices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
