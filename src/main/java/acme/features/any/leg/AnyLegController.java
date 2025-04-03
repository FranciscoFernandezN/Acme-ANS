package acme.features.any.leg;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.legs.Leg;
import acme.features.any.flight.AnyFlightListService;
import acme.features.any.flight.AnyFlightShowService;

@GuiController
public class AnyLegController extends AbstractGuiController<Any, Leg>{
	
	// Internal state ---------------------------------------------------------

	@Autowired
	AnyLegListService	listService;
	
	@Autowired
	AnyLegShowService	showService;


	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", showService);
	}
		
	
}
