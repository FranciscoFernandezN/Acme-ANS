
package acme.features.flightCrewMember.flightAssignment;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignments.CurrentStatus;
import acme.entities.flightAssignments.Duty;
import acme.entities.flightAssignments.FlightAssignment;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.AvailabilityStatus;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		int id = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
		Boolean isAvailable = this.repository.findFlightCrewMemberById(id).getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class) && isAvailable);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment = new FlightAssignment();
		flightAssignment.setIsDraftMode(true);
		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		Integer flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks");
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		// Verificar si el tripulante está disponible
		if (flightAssignment.getFlightCrewMember() == null)
			super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");
		else {
			if (flightAssignment.getFlightCrewMember().getAvailabilityStatus() != AvailabilityStatus.AVAILABLE)
				super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");

			// Obtener los Legs donde el tripulante ya está asignado
			List<Leg> assignedLegs = this.repository.findLegsByFlightCrewMemberId(flightAssignment.getFlightCrewMember().getId());

			// Verificar si hay solapamiento de horarios con otro Leg asignado
			boolean hasOverlappingLeg = assignedLegs.stream().anyMatch(leg -> flightAssignment.getLeg() != null && !leg.equals(flightAssignment.getLeg()) && leg.getScheduledDeparture().before(flightAssignment.getLeg().getScheduledArrival())
				&& leg.getScheduledArrival().after(flightAssignment.getLeg().getScheduledDeparture()));

			super.state(!hasOverlappingLeg, "flightCrewMember", "flight-crew-member.flight-assignment.error.overlapping-legs");
		}

		Date date = MomentHelper.getCurrentMoment();

		// Verificar que el Leg no sea null antes de acceder a su status
		if (flightAssignment.getLeg() != null) {
			// Verificar que el Leg no haya ocurrido y que su scheduledDeparture no sea futura
			boolean legHasOccurred = flightAssignment.getLeg().getStatus() == LegStatus.LANDED || flightAssignment.getLeg().getStatus() == LegStatus.CANCELLED;
			boolean legIsInFuture = flightAssignment.getLeg().getScheduledDeparture().after(date);

			if (legHasOccurred || !legIsInFuture)
				super.state(false, "leg", "flight-crew-member.flight-assignment.error.already-occurred-or-future");
		} else
			super.state(false, "leg", "flight-crew-member.flight-assignment.error.leg-null");

		// Limitar el número de pilotos y copilotos en el Leg
		if (flightAssignment.getDuty() == Duty.PILOT) {
			long pilotCount = this.repository.findAllFlightAssignments().stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.PILOT).count();
			super.state(pilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.pilot-limit-exceeded");
		}

		if (flightAssignment.getDuty() == Duty.COPILOT) {
			long copilotCount = this.repository.findAllFlightAssignments().stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.COPILOT).count();
			super.state(copilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.copilot-limit-exceeded");
		}

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.save(flightAssignment);
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
		legs = this.repository.findAllLegs().stream().filter(leg -> !leg.getIsDraftMode() && leg.getStatus() != LegStatus.LANDED && leg.getStatus() != LegStatus.CANCELLED && leg.getScheduledDeparture().after(date)).toList();
		// Obtener todos los FlightCrewMembers
		flightCrewMembers = this.repository.findAllFlightCrewMembers();

		// Crear opciones de selección para Duty, Current Status y FlightCrewMember
		dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		currentStatuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());

		legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());
		// Desvincular los datos del FlightAssignment
		dataset = super.unbindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks", "isDraftMode");

		// Colocar las opciones en el Dataset
		dataset.put("duties", dutyChoices);
		dataset.put("currentStatus", currentStatuses);
		dataset.put("legs", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey()); // Validación segura
		dataset.put("flightCrewMembers", flightCrewMemberChoices);
		dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());
		dataset.put("isAvailable", isAvailable);

		// Enviar los datos a la respuesta
		super.getResponse().addData(dataset);
	}

}
