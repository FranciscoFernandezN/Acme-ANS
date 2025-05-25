
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

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(id);

		boolean status = assignment != null && assignment.getFlightCrewMember() != null && super.getRequest().getPrincipal().hasRealm(assignment.getFlightCrewMember());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

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

		// Obtener todos los Legs publicados
		List<Leg> publishedLegs = this.repository.findPublishedLegs();

		// Si la asignación NO está en draft mode, filtrar los Legs válidos
		if (flightAssignment.getIsDraftMode())
			legs = publishedLegs.stream().filter(leg -> leg.getStatus() != LegStatus.LANDED && leg.getStatus() != LegStatus.CANCELLED && leg.getScheduledDeparture().after(date)).toList();
		else
			legs = publishedLegs.stream().filter(leg -> leg.getStatus() != LegStatus.LANDED).toList();

		// Crear opciones de selección para Duty
		dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());

		// Crear opciones de selección para Current Status
		SelectChoices currentStatuses = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		// Crear select de Legs SIN marcar ninguno
		legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());

		// Valor seleccionado (aunque no esté en las opciones)
		String selectedLegKey = flightAssignment.getLeg() != null ? String.valueOf(flightAssignment.getLeg().getId()) : null;

		// Desvincular los datos del FlightAssignment
		dataset = super.unbindObject(flightAssignment, "duty", "lastUpDate", "currentStatus", "remarks", "isDraftMode");

		// Colocar los datos en el dataset
		dataset.put("duties", dutyChoices);
		dataset.put("currentStatus", currentStatuses);
		dataset.put("legs", legChoices);
		dataset.put("leg", selectedLegKey); // Evita null pointer
		dataset.put("isAvailable", isAvailable);
		dataset.put("flightCrewMember", flightAssignment.getFlightCrewMember().getEmployeeCode());

		// Añadir a la respuesta
		super.getResponse().addData(dataset);

	}
}
