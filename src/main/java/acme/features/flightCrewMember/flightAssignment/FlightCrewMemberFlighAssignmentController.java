
package acme.features.flightCrewMember.flightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignments.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiController
public class FlightCrewMemberFlighAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberFlightAssignmentShowService			showService;

	@Autowired
	private FlightCrewMemberFlightAssignmentCreateService		createService;

	@Autowired
	private FlightCrewMemberFlightAssignmentDeleteService		deleteService;

	@Autowired
	private FlightCrewMemberFlightAssignmentUpdateService		updateService;

	@Autowired
	private FlightCrewMemberFlightAssignmentPublishService		publishService;

	@Autowired
	private FlightCrewMemberFlightAssignmentListBeforeService	listBeforeCurrentService;

	@Autowired
	private FlightCrewMemberFlightAssignmentListAfterService	listAfterCurrentService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addCustomCommand("list-before-current", "list", this.listBeforeCurrentService);
		super.addCustomCommand("list-after-current", "list", this.listAfterCurrentService);
	}

}
