
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
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class));
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		int flightAssignmentId;

		flightAssignmentId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

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
		FlightAssignment original = this.repository.findFlightAssignmentById(flightAssignment.getId());

		// Verificar si el FlightAssignment está en modo borrador
		if (original != null && !original.getIsDraftMode()) {
			super.state(false, "*", "flight-crew-member.flight-assignment.error.cannot-modify-published");
			return;
		}

		// Verificar si el FlightCrewMember ha cambiado
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

		// Verificar si el Duty ha cambiado
		boolean hasChangedDuty = original == null || !original.getDuty().equals(flightAssignment.getDuty());
		if (hasChangedDuty)
			if (flightAssignment.getDuty() == null)
				super.state(false, "duty", "flight-crew-member.flight-assignment.error.invalid-duty");
			else {
				// Validar restricciones de cantidad de pilotos y copilotos en el Leg
				long pilotCount = this.repository.findFlightAssignmentBeforeCurrent(date).stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.PILOT).count();
				long copilotCount = this.repository.findFlightAssignmentBeforeCurrent(date).stream().filter(fa -> fa.getLeg().equals(flightAssignment.getLeg()) && fa.getDuty() == Duty.COPILOT).count();

				if (flightAssignment.getDuty() == Duty.PILOT)
					super.state(pilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.pilot-limit-exceeded");
				if (flightAssignment.getDuty() == Duty.COPILOT)
					super.state(copilotCount < 1, "duty", "flight-crew-member.flight-assignment.error.copilot-limit-exceeded");
			}

		// Verificar si el CurrentStatus ha cambiado
		boolean hasChangedStatus = original == null || !original.getCurrentStatus().equals(flightAssignment.getCurrentStatus());
		if (hasChangedStatus)
			if (flightAssignment.getCurrentStatus() == null)
				super.state(false, "currentStatus", "flight-crew-member.flight-assignment.error.invalid-status");
			else if (flightAssignment.getCurrentStatus() == CurrentStatus.CONFIRMED && (flightAssignment.getLeg() == null || flightAssignment.getLeg().getStatus() != LegStatus.LANDED))
				super.state(false, "currentStatus", "flight-crew-member.flight-assignment.error.invalid-completion-status");

		// Verificar que el Leg no sea null antes de acceder a su status
		if (flightAssignment.getLeg() != null) {
			if (flightAssignment.getLeg().getStatus() == LegStatus.LANDED || flightAssignment.getLeg().getStatus() == LegStatus.CANCELLED)
				super.state(false, "leg", "flight-crew-member.flight-assignment.error.already-occurred");
		} else
			super.state(false, "leg", "flight-crew-member.flight-assignment.error.leg-null");

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
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

		super.getResponse().addData(dataset);
	}
}
