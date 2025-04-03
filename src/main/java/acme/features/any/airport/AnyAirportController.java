
package acme.features.any.airport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.airports.Airport;

@GuiController
public class AnyAirportController extends AbstractGuiController<Any, Airport> {
	// Internal state ---------------------------------------------------------

	@Autowired
	AnyAirportListService	listService;

	@Autowired
	AnyAirportShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
}
