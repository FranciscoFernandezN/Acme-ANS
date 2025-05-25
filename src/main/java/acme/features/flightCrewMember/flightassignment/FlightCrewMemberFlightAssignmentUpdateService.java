
package acme.features.flightCrewMember.flightassignment;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignments.CurrentStatus;
import acme.entities.flightassignments.Duty;
import acme.entities.flightassignments.FlightAssignment;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.AvailabilityStatus;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean isAuthorised = false;

		// Verificar si el principal es de tipo FlightCrewMember
		if (super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class)) {
			int id = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
			FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(id);

			int flightAssignmentId = super.getRequest().getData("id", int.class);
			FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
			Date date = MomentHelper.getCurrentMoment();
			if (flightAssignment != null) {
				// Verificar que el Leg no sea null antes de acceder a su status
				Leg leg = flightAssignment.getLeg();
				if (leg != null) {
					// Verificar que el Leg no haya ocurrido y que su scheduledDeparture sea futura
					boolean legHasOccurred = leg.getStatus() == LegStatus.LANDED || leg.getStatus() == LegStatus.CANCELLED;
					boolean legIsInFuture = leg.getScheduledDeparture().after(date);

					if (legHasOccurred || !legIsInFuture)
						super.state(false, "leg", "flight-crew-member.flight-assignment.error.already-occurred-or-future");
					else
						isAuthorised = true; // La autorización es válida si las condiciones anteriores no fallan
				} else
					super.state(false, "leg", "flight-crew-member.flight-assignment.error.leg-null");
			}
		} else
			super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");
		int id = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
		Boolean isAvailable = this.repository.findFlightCrewMemberById(id).getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
		super.getResponse().setAuthorised(isAuthorised && isAvailable);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		if (flightAssignment != null)
			super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		int id = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(id);

		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks");
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		if (flightAssignment.getFlightCrewMember() == null)
			super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");
		else {
			if (flightAssignment.getFlightCrewMember().getAvailabilityStatus() != AvailabilityStatus.AVAILABLE)
				super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");

			// Obtener las asignaciones del miembro de la tripulación, excluyendo la actual
			List<FlightAssignment> otherAssignments = this.repository.findAllFlightAssignments().stream().filter(fa -> fa.getFlightCrewMember().getId() == flightAssignment.getFlightCrewMember().getId()) // Mismo miembro
				.filter(fa -> fa.getId() != flightAssignment.getId()) // Excluir la asignación actual
				.collect(Collectors.toList());

			// Comprobar solapamiento de legs
			boolean hasOverlappingLeg = otherAssignments.stream().map(FlightAssignment::getLeg).filter(Objects::nonNull)
				.anyMatch(leg -> flightAssignment.getLeg() != null && leg.getScheduledDeparture().before(flightAssignment.getLeg().getScheduledArrival()) && leg.getScheduledArrival().after(flightAssignment.getLeg().getScheduledDeparture()));

			super.state(!hasOverlappingLeg, "flightCrewMember", "flight-crew-member.flight-assignment.error.overlapping-legs");
		}

		Date date = MomentHelper.getCurrentMoment();

		if (flightAssignment.getLeg() != null) {
			boolean legHasOccurred = flightAssignment.getLeg().getStatus() == LegStatus.LANDED || flightAssignment.getLeg().getStatus() == LegStatus.CANCELLED;
			boolean legIsInFuture = flightAssignment.getLeg().getScheduledDeparture().after(date);

			if (legHasOccurred || !legIsInFuture)
				super.state(false, "leg", "flight-crew-member.flight-assignment.error.already-occurred-or-future");
		} else
			super.state(false, "leg", "flight-crew-member.flight-assignment.error.leg-null");

		// Validación para piloto y copiloto, excluyendo la propia asignación
		List<FlightAssignment> allAssignments = this.repository.findAllFlightAssignments().stream().filter(fa -> fa.getId() != flightAssignment.getId()) // ⚠️ comparación por valor int
			.collect(Collectors.toList());

		if (flightAssignment.getDuty() == Duty.PILOT) {
			long pilotCount = allAssignments.stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.PILOT).count();

			super.state(pilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.pilot-limit-exceeded");
		}

		if (flightAssignment.getDuty() == Duty.COPILOT) {
			long copilotCount = allAssignments.stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.COPILOT).count();

			super.state(copilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.copilot-limit-exceeded");
		}

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		// Guardar el vuelo asignado
		flightAssignment.setLastUpDate(MomentHelper.getCurrentMoment());
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		SelectChoices dutyChoices, legChoices;
		List<Leg> legs;
		Dataset dataset;
		Date date;

		date = MomentHelper.getCurrentMoment();

		// Obtener el ID del FlightCrewMember actual
		int id = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
		Boolean isAvailable = this.repository.findFlightCrewMemberById(id).getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);

		// Filtrar los Legs válidos (publicados, no cancelados/aterrizados, con salida futura)
		legs = this.repository.findPublishedLegs().stream().filter(leg -> leg.getStatus() != LegStatus.LANDED && leg.getStatus() != LegStatus.CANCELLED && leg.getScheduledDeparture().after(date)).toList();

		// Crear opciones de selección para Duty, Current Status y FlightCrewMember
		dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		SelectChoices currentStatuses = new SelectChoices();
		currentStatuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());
		// Desvincular los datos del FlightAssignment
		dataset = super.unbindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks", "isDraftMode");

		// Colocar las opciones en el Dataset
		dataset.put("duties", dutyChoices);
		dataset.put("currentStatus", currentStatuses);
		dataset.put("legs", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey()); // Validación segura
		dataset.put("isAvailable", isAvailable);
		dataset.put("flightCrewMember", flightAssignment.getFlightCrewMember().getEmployeeCode());
		// Enviar los datos a la respuesta
		super.getResponse().addData(dataset);
	}
}
