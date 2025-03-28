package acme.features.any.flight;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractController;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flights.Flight;
import acme.features.any.review.AnyReviewCreateService;
import acme.features.any.review.AnyReviewListService;

@GuiController
public class AnyFlightController extends AbstractGuiController<Any, Flight> {
	
	// Internal state ---------------------------------------------------------

	@Autowired
	AnyFlightListService	listService;

	@Autowired
	AnyFlightShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
	
}
