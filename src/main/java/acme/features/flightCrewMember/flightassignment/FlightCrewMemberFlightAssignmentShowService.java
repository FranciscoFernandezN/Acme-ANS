
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
public class FlightCrewMemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(id);

		boolean status = assignment != null && super.getRequest().getPrincipal().hasRealm(assignment.getFlightCrewMember());

		super.getResponse().setAuthorised(status);
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
