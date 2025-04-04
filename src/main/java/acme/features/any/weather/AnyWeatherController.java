
package acme.features.any.weather;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.weather.Weather;

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
