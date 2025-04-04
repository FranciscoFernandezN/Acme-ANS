
package acme.features.flightCrewMember.flightAssignment;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignments.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.AvailabilityStatus;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentListAfterService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class));
	}

	@Override
	public void load() {
		List<FlightAssignment> flightAssignments;
		Date date;
		date = MomentHelper.getCurrentMoment();

		flightAssignments = this.repository.findFlightAssignmentAfterCurrent(date);

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		SelectChoices dutyChoices, currentStatuses, flightCrewMemberChoices, legChoices;
		List<Leg> legs;
		List<FlightCrewMember> flightCrewMembers;
		Dataset dataset;
		Date date;

		date = MomentHelper.getCurrentMoment();

		// Obtener el ID del FlightCrewMember actual
		int id = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
		Boolean isAvailable = this.repository.findFlightCrewMemberById(id).getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);

		// Filtrar los Legs válidos (publicados, no cancelados/aterrizados, con salida futura)
		//legs = this.repository.findAllLegs().stream().filter(leg -> !leg.getIsDraftMode() && leg.getStatus() != LegStatus.LANDED && leg.getStatus() != LegStatus.CANCELLED && leg.getScheduledDeparture().after(date)).toList();
		// Obtener todos los FlightCrewMembers
		flightCrewMembers = this.repository.findAllFlightCrewMembers();

		// Crear opciones de selección para Duty, Current Status y FlightCrewMember
		//dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		//currentStatuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		//flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());

		//legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());
		// Desvincular los datos del FlightAssignment
		dataset = super.unbindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks", "isDraftMode");

		// Colocar las opciones en el Dataset
		//dataset.put("duties", dutyChoices);
		//dataset.put("currentStatus", currentStatuses);
		//dataset.put("legs", legChoices);
		//dataset.put("leg", legChoices.getSelected().getKey()); // Validación segura
		//dataset.put("flightCrewMembers", flightCrewMemberChoices);
		//dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());
		super.getResponse().addGlobal("isAvailable", isAvailable);

		// Enviar los datos a la respuesta
		super.getResponse().addData(dataset);
	}
}
