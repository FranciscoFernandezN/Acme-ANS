
package acme.features.administrator.passenger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.passengers.Passenger;

@GuiController
public class AdministratorPassengerController extends AbstractGuiController<Administrator, Passenger> {

	final static String											MASTER_ID	= "bookingId";

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorPassengerListService					listService;

	@Autowired
	private AdministratorBannedPassengersListService			listBannedPassengerService;

	@Autowired
	private AdministratorBannedPassengersLastMonthListService	listBannedPassengerLastMonthService;

	@Autowired
	private AdministratorLiftedBanPassengersListService			listLiftedBanPassengerService;

	@Autowired
	private AdministratorPassengerShowService					showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addCustomCommand("list-banned-passengers", "list", this.listBannedPassengerService);
		super.addCustomCommand("list-banned-passengers-last-month", "list", this.listBannedPassengerLastMonthService);
		super.addCustomCommand("list-lifted-ban-passengers", "list", this.listLiftedBanPassengerService);
	}

}
