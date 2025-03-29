
package acme.features.administrator.aircraft;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.aircrafts.Aircraft;

@GuiController
public class AdministratorAircraftController extends AbstractGuiController<Administrator, Aircraft> {
	// Internal state ---------------------------------------------------------

	@Autowired
	AdministratorAircraftListService	listService;

	@Autowired
	AdministratorAircraftShowService	showService;

	@Autowired
	AdministratorAircraftUpdateService	updateService;

	@Autowired
	AdministratorAircraftCreateService	createService;

	@Autowired
	AdministratorAircraftEnableService	enableService;

	@Autowired
	AdministratorAircraftDisableService	disableService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("enable", this.enableService);
		super.addBasicCommand("disable", this.disableService);
	}
}
