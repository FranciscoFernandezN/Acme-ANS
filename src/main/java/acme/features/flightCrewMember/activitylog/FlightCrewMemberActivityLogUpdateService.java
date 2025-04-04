
package acme.features.flightCrewMember.activitylog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylogs.ActivityLog;
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

		boolean isMember = activityLog.getFlightCrewMember() != null && super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

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
		int flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);
		super.bindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");

		activityLog.setFlightCrewMember(flightCrewMember);
		activityLog.setLeg(leg);
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		if (activityLog.getFlightCrewMember() != null) {
			FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(activityLog.getFlightCrewMember().getId());
			super.state(flightCrewMember != null, "employeeCode", "flight-crew-member.activity-log.error.invalid-flight-crew-member");
		}

		if (activityLog.getLeg() != null) {
			Leg leg = this.repository.findLegById(activityLog.getLeg().getId());
			super.state(leg != null, "flightNumber", "flight-crew-member.activity-log.error.invalid-leg");
		}

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
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

		super.getResponse().addData(dataset);
	}

}
