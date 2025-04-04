
package acme.features.flightCrewMember.activityLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLogs.ActivityLog;
import acme.entities.legs.Leg;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class));
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
		Dataset dataset;
		SelectChoices flightCrewMemberChoices, legChoices;
		List<Leg> legs;
		List<FlightCrewMember> flightCrewMembers;

		// Filtrar los Legs válidos (publicados, no cancelados/aterrizados, con salida futura)
		legs = this.repository.findAllLegs();
		// Obtener todos los FlightCrewMembers
		flightCrewMembers = this.repository.findAllFlightCrewMembers();

		flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", activityLog.getFlightCrewMember());

		legChoices = SelectChoices.from(legs, "flightNumber", activityLog.getLeg());

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "isDraftMode");
		dataset.put("legs", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey()); // Validación segura
		dataset.put("flightCrewMembers", flightCrewMemberChoices);
		dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());
		dataset.put("registrationMoment", MomentHelper.getCurrentMoment());

		super.getResponse().addData(dataset);
	}
}
