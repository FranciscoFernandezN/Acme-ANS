
package acme.features.flightCrewMember.activitylog;

import java.util.Collection;

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
public class FlightCrewMemberActivityLogUpdateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

		if (activityLog == null) {
			super.getResponse().setAuthorised(false);
			return; // Terminar si no existe el registro de actividad.
		}

		boolean isMember = activityLog.getFlightAssignment().getFlightCrewMember() != null && super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		boolean isDraft = activityLog.getIsDraftMode() != null && activityLog.getIsDraftMode();

		super.getResponse().setAuthorised(isMember && isDraft);
	}

	@Override
	public void load() {
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel", "flightAssignment");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		FlightAssignment assignment = activityLog.getFlightAssignment();
		if (assignment == null) {
			super.state(false, "flightAssignment", "javax.validation.constraints.NotNull.message");
			return;
		}

		Leg leg = assignment.getLeg();
		if (leg == null || leg.getScheduledDeparture() == null) {
			super.state(false, "leg", "javax.validation.constraints.NotNull.message");
			return;
		}

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		this.repository.save(activityLog);
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
