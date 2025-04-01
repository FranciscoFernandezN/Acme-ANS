
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
public class FlightCrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		FlightAssignment flightAssignment;

		flightAssignment = new FlightAssignment();

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		Integer flightCrewMemberId;
		FlightCrewMember flightCrewMember;

		flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		Integer legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks");
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		Date date;
		date = MomentHelper.getCurrentMoment();
		FlightAssignment original = this.repository.findFlightAssignmentById(flightAssignment.getId()); // Obtener el original

		// Verificar si el FlightAssignment está en modo borrador o ya ha sido publicado
		if (original != null && !original.getIsDraftMode()) {
			super.state(false, "*", "flight-crew-member.flight-assignment.error.cannot-modify-published");
			return;
		}

		// Validación de disponibilidad y asignación
		boolean hasChangedCrewMember = original == null || !original.getFlightCrewMember().equals(flightAssignment.getFlightCrewMember());

		if (hasChangedCrewMember)
			if (flightAssignment.getFlightCrewMember() == null)
				super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");
			else {
				if (flightAssignment.getFlightCrewMember().getAvailabilityStatus() != AvailabilityStatus.AVAILABLE)
					super.state(false, "flightCrewMember", "flight-crew-member.flight-assignment.error.not-available");

				// Verificar que no esté asignado a otro Leg
				List<Leg> assignedLegs = this.repository.findLegsByFlightCrewMemberId(flightAssignment.getFlightCrewMember().getId());
				boolean isAssignedToAnotherLeg = assignedLegs.contains(flightAssignment.getLeg());
				super.state(!isAssignedToAnotherLeg, "flightCrewMember", "flight-crew-member.flight-assignment.error.already-assigned");
			}

		// Validación de Leg
		if (flightAssignment.getLeg() != null) {
			if (flightAssignment.getLeg().getStatus() == LegStatus.LANDED || flightAssignment.getLeg().getStatus() == LegStatus.CANCELLED)
				super.state(false, "leg", "flight-crew-member.flight-assignment.error.already-occurred");
		} else
			super.state(false, "leg", "flight-crew-member.flight-assignment.error.leg-null");

		// Validación de límite de pilotos y copilotos
		if (flightAssignment.getDuty() == Duty.PILOT) {
			long pilotCount = this.repository.findFlightAssignmentBeforeCurrent(date).stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.PILOT).count();
			super.state(pilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.pilot-limit-exceeded");
		}

		if (flightAssignment.getDuty() == Duty.COPILOT) {
			long copilotCount = this.repository.findFlightAssignmentBeforeCurrent(date).stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.COPILOT).count();
			super.state(copilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.copilot-limit-exceeded");
		}

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
		Dataset dataset;

		SelectChoices dutyChoices, currentStatuses, flightCrewMemberChoices, legChoices;
		List<Leg> legs;
		List<FlightCrewMember> flightCrewMembers;

		legs = this.repository.findAllLegs();
		flightCrewMembers = this.repository.findAllFlightCrewMembers();

		dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		currentStatuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());
		legChoices = SelectChoices.from(legs, "uniqueIdentifier", flightAssignment.getLeg());

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks", "isDraftMode");

		dataset.put("duties", dutyChoices);
		dataset.put("currentStatus", currentStatuses);
		dataset.put("legs", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", flightCrewMemberChoices);
		dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());

		if (!flightAssignment.getIsDraftMode())
			dataset.put("disabled", true);

		super.getResponse().addData(dataset);
	}
}
