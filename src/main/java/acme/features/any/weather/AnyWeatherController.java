package acme.features.any.weather;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.weather.Weather;
import acme.features.manager.leg.ManagerLegCreateService;
import acme.features.manager.leg.ManagerLegDeleteService;
import acme.features.manager.leg.ManagerLegListService;
import acme.features.manager.leg.ManagerLegShowService;
import acme.features.manager.leg.ManagerLegUpdateService;

@GuiController
public class AnyWeatherController extends AbstractGuiController<Any, Weather> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyWeatherListService	listService;

	@Autowired
	private AnyWeatherShowService	showService;


	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
}
