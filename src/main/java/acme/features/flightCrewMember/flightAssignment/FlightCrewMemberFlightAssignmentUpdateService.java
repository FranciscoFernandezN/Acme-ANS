
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

			if (flightAssignment != null) {
				// Verificar que el Leg no sea null antes de acceder a su status
				Leg leg = flightAssignment.getLeg();
				if (leg != null) {
					// Verificar que el Leg no haya ocurrido y que su scheduledDeparture sea futura
					boolean legHasOccurred = leg.getStatus() == LegStatus.LANDED || leg.getStatus() == LegStatus.CANCELLED;
					boolean legIsInFuture = leg.getScheduledDeparture().after(new Date());

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
		int flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		int legId = super.getRequest().getData("leg", int.class);
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

		// Conservar los valores originales si no se han actualizado
		if (flightAssignment.getFlightCrewMember() == null && originalFlightAssignment != null)
			flightAssignment.setFlightCrewMember(originalFlightAssignment.getFlightCrewMember());

		if (flightAssignment.getLeg() == null && originalFlightAssignment != null)
			flightAssignment.setLeg(originalFlightAssignment.getLeg());

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

		// Validaciones restantes
		if (flightAssignment.getFlightCrewMember() == null)
			super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");
		else {
			boolean isSameFlightCrewMember = originalFlightAssignment != null && flightAssignment.getFlightCrewMember().equals(originalFlightAssignment.getFlightCrewMember());

			if (!isSameFlightCrewMember && flightAssignment.getFlightCrewMember().getAvailabilityStatus() != AvailabilityStatus.AVAILABLE)
				super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");

			List<Leg> assignedLegs = this.repository.findLegsByFlightCrewMemberId(flightAssignment.getFlightCrewMember().getId());

			boolean hasOverlappingLeg = assignedLegs.stream().anyMatch(leg -> flightAssignment.getLeg() != null && !leg.equals(flightAssignment.getLeg()) && leg.getScheduledDeparture().before(flightAssignment.getLeg().getScheduledArrival())
				&& leg.getScheduledArrival().after(flightAssignment.getLeg().getScheduledDeparture()));

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

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		// Guardar el vuelo asignado
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		SelectChoices dutyChoices, currentStatuses, flightCrewMemberChoices, legChoices, availabilityChoices;
		List<Leg> legs;
		List<FlightCrewMember> flightCrewMembers;
		Dataset dataset;
		Date date;
		date = MomentHelper.getCurrentMoment();

		int id = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
		Boolean isAvailable = this.repository.findFlightCrewMemberById(id).getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);

		legs = this.repository.findAllLegs();

		// Obtener todos los FlightCrewMembers disponibles
		flightCrewMembers = this.repository.findAllFlightCrewMembers();
		// Crear opciones de selección
		dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		currentStatuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());

		// Generar SelectChoices solo con los Legs válidos
		legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());

		// Desvincular los datos
		dataset = super.unbindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks", "isDraftMode");

		// Colocar los valores de las selecciones
		dataset.put("duties", dutyChoices);
		dataset.put("currentStatus", currentStatuses);
		dataset.put("legs", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", flightCrewMemberChoices);
		dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());
		dataset.put("isAvailable", isAvailable);

		super.getResponse().addData(dataset);
	}
}
