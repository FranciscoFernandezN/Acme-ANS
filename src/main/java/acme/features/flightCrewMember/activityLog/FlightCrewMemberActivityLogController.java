
package acme.features.flightCrewMember.activityLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.activityLogs.ActivityLog;
import acme.features.administrator.aircraft.AdministratorAircraftDisableService;
import acme.features.administrator.aircraft.AdministratorAircraftListService;
import acme.features.administrator.aircraft.AdministratorAircraftShowService;
import acme.features.administrator.aircraft.AdministratorAircraftUpdateService;
import acme.realms.FlightCrewMember;

@GuiController
public class FlightCrewMemberActivityLogController extends AbstractGuiController<FlightCrewMember, ActivityLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	AdministratorAircraftListService	listService;

	@Autowired
	AdministratorAircraftShowService	showService;

	@Autowired
	AdministratorAircraftUpdateService	updateService;

	@Autowired
	FlightCrewMemberCreateService		createService;

	@Autowired
	AdministratorAircraftDisableService	disableService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
