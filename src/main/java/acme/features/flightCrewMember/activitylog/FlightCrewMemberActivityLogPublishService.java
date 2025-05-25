
package acme.features.flightCrewMember.activitylog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylogs.ActivityLog;
import acme.entities.flightassignments.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

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

		boolean isMember = activityLog.getFlightAssignment().getFlightCrewMember() != null && super.getRequest().getPrincipal().hasRealm(activityLog.getFlightAssignment().getFlightCrewMember());

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
		super.bindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "flightAssignment");

	}

	@Override
	public void validate(final ActivityLog activityLog) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		// Validar si 'flightAssignment' o 'draftMode' son nulos
		if (activityLog != null && activityLog.getFlightAssignment() != null) {
			boolean isDraftMode = activityLog.getFlightAssignment().getIsDraftMode();
			if (isDraftMode)
				// Si 'draftMode' es verdadero, agregar un mensaje de error
				super.state(false, "*", "flight-crew-member.activity-log.flight-assignment.draftMode");
		}

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setIsDraftMode(false);
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
