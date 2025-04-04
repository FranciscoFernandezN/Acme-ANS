
package acme.features.any.weather;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.weather.Weather;

@GuiService
public class AnyWeatherListService extends AbstractGuiService<Any, Weather> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyWeatherRepository wr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		List<Weather> weather;

		weather = this.wr.findAllWeather();

		super.getBuffer().addData(weather);
	}

	@Override
	public void unbind(final Weather weather) {
		Dataset dataset;

		dataset = super.unbindObject(weather, "forecastDate", "temperature");

		dataset.put("status", weather.getStatus());
		dataset.put("city", weather.getCity());

		super.getResponse().addData(dataset);
	}

}
