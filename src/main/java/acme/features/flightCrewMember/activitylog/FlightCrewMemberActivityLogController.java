
package acme.features.flightCrewMember.activityLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.activityLogs.ActivityLog;
import acme.realms.FlightCrewMember;

@GuiController
public class FlightCrewMemberActivityLogController extends AbstractGuiController<FlightCrewMember, ActivityLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	FlightCrewMemberActivityLogListService		listService;

	@Autowired
	FlightCrewMemberActivityLogShowService		showService;

	@Autowired
	FlightCrewMemberActivityLogUpdateService	updateService;

	@Autowired
	FlightCrewMemberActivityLogCreateService	createService;

	@Autowired
	FlightCrewMemberActivityLogDeleteService	deleteService;

	@Autowired
	FlightCrewMemberActivityLogPublishService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
