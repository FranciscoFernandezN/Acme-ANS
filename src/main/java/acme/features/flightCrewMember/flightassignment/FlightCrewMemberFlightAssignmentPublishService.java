
package acme.features.flightCrewMember.flightassignment;

import java.util.Date;
import java.util.List;

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
public class FlightCrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		FlightAssignment flightAssignment;

		flightAssignment = new FlightAssignment();

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		int id = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(id);

		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks");
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		// Obtener el FlightAssignment original antes de la edición
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment originalFlightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		// Verificar si el tripulante está disponible
		if (flightAssignment.getFlightCrewMember() == null)
			super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");
		else {
			// Comprobar si el tripulante es el mismo que el original
			boolean isSameFlightCrewMember = originalFlightAssignment != null && flightAssignment.getFlightCrewMember().equals(originalFlightAssignment.getFlightCrewMember());

			// Si el tripulante no es el mismo, comprobar su disponibilidad
			if (!isSameFlightCrewMember && flightAssignment.getFlightCrewMember().getAvailabilityStatus() != AvailabilityStatus.AVAILABLE)
				super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");

			// Obtener los Legs donde el tripulante ya está asignado
			List<Leg> assignedLegs = this.repository.findLegsByFlightCrewMemberId(flightAssignment.getFlightCrewMember().getId());

			// Verificar si hay solapamiento de horarios con otro Leg asignado
			boolean hasOverlappingLeg = assignedLegs.stream()
				.anyMatch(leg -> flightAssignment.getLeg() != null && leg.getScheduledDeparture().before(flightAssignment.getLeg().getScheduledArrival()) && leg.getScheduledArrival().after(flightAssignment.getLeg().getScheduledDeparture()));

			super.state(!hasOverlappingLeg, "flightCrewMember", "flight-crew-member.flight-assignment.error.overlapping-legs");
		}

		// Verificar que el Leg no sea null antes de acceder a su status
		Date date = MomentHelper.getCurrentMoment();
		if (flightAssignment.getLeg() != null) {
			// Verificar que el Leg no haya ocurrido y que su scheduledDeparture no sea futura
			boolean legHasOccurred = flightAssignment.getLeg().getStatus() == LegStatus.LANDED || flightAssignment.getLeg().getStatus() == LegStatus.CANCELLED;
			boolean legIsInFuture = flightAssignment.getLeg().getScheduledDeparture().after(date);

			if (legHasOccurred || !legIsInFuture)
				super.state(false, "leg", "flight-crew-member.flight-assignment.error.already-occurred-or-future");
		} else
			super.state(false, "leg", "flight-crew-member.flight-assignment.error.leg-null");

		// Validar Duty si se ha cambiado
		if (flightAssignment.getDuty() != null && !flightAssignment.getDuty().equals(originalFlightAssignment.getDuty())) {
			if (flightAssignment.getDuty() == Duty.PILOT) {
				long pilotCount = this.repository.findAllFlightAssignments().stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.PILOT).count();
				super.state(pilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.pilot-limit-exceeded");
			}

			if (flightAssignment.getDuty() == Duty.COPILOT) {
				long copilotCount = this.repository.findAllFlightAssignments().stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.COPILOT).count();
				super.state(copilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.copilot-limit-exceeded");
			}
		}

		if (flightAssignment.getCurrentStatus() == CurrentStatus.PENDING)
			super.state(false, "currentStatus", "flight-crew-member.flight-assignment.error.cannot-publish-pending");

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setIsDraftMode(false);  // Set isDraftMode to false, marking the assignment as published
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

		CurrentStatus selected = flightAssignment.getCurrentStatus();

		// Opción por defecto (como hace la clase internamente)
		currentStatuses.add("0", "----", selected == null);

		// Agregamos solo los valores que queremos mostrar
		for (CurrentStatus status : List.of(CurrentStatus.PENDING, CurrentStatus.CONFIRMED)) {
			String key = status.toString();
			String label = status.toString(); // Puedes usar algo más amigable si quieres
			boolean isSelected = status.equals(selected);
			currentStatuses.add(key, label, isSelected);
		}

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
