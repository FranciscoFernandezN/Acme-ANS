
package acme.features.administrator.ban;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.passengers.Ban;

@GuiController
public class AdministratorBanController extends AbstractGuiController<Administrator, Ban> {

	// Internal state ---------------------------------------------------------

	@Autowired
	AdministratorBanListService		listService;

	@Autowired
	AdministratorBanShowService		showService;

	@Autowired
	AdministratorBanUpdateService	updateService;

	@Autowired
	AdministratorBanCreateService	createService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
	}

}
