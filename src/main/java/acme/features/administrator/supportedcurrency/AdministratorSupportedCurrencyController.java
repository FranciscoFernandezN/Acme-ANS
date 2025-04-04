
package acme.features.administrator.supportedcurrency;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiController
public class AdministratorSupportedCurrencyController extends AbstractGuiController<Administrator, SupportedCurrency> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorSupportedCurrencyShowService	showService;

	@Autowired
	private AdministratorSupportedCurrencyUpdateService	updateService;

	@Autowired
	private AdministratorSupportedCurrencyCreateService	createService;

	@Autowired
	private AdministratorSupportedCurrencyListService	listService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("create", this.createService);
	}

}
