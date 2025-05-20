
package acme.features.flightCrewMember.flightassignment;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignments.FlightAssignment;
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
		Date date = MomentHelper.getCurrentMoment();

		int crewId = super.getRequest().getPrincipal().getRealmOfType(FlightCrewMember.class).getId();
		flightAssignments = this.repository.findFlightAssignmentAfterCurrentByCrewMember(crewId, date);

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {

		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpDate", "currentStatus");

		// Enviar los datos a la respuesta
		super.getResponse().addData(dataset);
	}
}
