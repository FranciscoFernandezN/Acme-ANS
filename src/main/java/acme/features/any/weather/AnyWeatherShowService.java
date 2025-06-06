
package acme.features.any.weather;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.weather.Weather;

@GuiService
public class AnyWeatherShowService extends AbstractGuiService<Any, Weather> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyWeatherRepository wr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Weather weather;
		int weatherId;
		weatherId = super.getRequest().getData("id", int.class);
		weather = this.wr.findWeatherById(weatherId);
		
		super.getResponse().setAuthorised(weather != null);
	}

	@Override
	public void load() {
		Weather weather;
		int weatherId;

		weatherId = super.getRequest().getData("id", int.class);
		weather = this.wr.findWeatherById(weatherId);

		super.getBuffer().addData(weather);
	}

	@Override
	public void unbind(final Weather weather) {
		Dataset dataset;

		dataset = super.unbindObject(weather, "forecastDate", "temperature", "visibility", "rainPerHour", "snowPerHour", "wind");

		dataset.put("status", weather.getStatus());
		dataset.put("city", weather.getCity());

		super.getResponse().addData(dataset);
	}

}
