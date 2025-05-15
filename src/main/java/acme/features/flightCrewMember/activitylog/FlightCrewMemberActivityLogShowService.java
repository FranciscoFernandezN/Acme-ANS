
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
public class FlightCrewMemberActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		boolean status = activityLog != null && super.getRequest().getPrincipal().hasRealm(activityLog.getFlightAssignment().getFlightCrewMember());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int activityLogId;
		ActivityLog activityLog;

		activityLogId = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(activityLogId);

		super.getBuffer().addData(activityLog);
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
