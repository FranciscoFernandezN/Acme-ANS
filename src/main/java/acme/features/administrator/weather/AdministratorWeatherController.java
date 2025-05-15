
package acme.features.administrator.weather;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import acme.client.components.principals.Administrator;
import acme.client.controllers.GuiController;
import acme.client.helpers.Assert;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.components.weather.WeatherPOJO;
import acme.entities.weather.Weather;

@GuiController
public class AdministratorWeatherController {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorWeatherRepository repository;

	// Endpoints --------------------------------------------------------------


	@GetMapping("/administrator/weather/populate")
	public ModelAndView populateInitial() {
		Assert.state(PrincipalHelper.get().hasRealmOfType(Administrator.class), "acme.default.error.not-authorised");

		return this.doPopulate();
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView doPopulate() {

		List<String> cities = this.repository.findAllCities();
		List<Weather> weather = cities.stream().map(c -> this.findWeatherOfCity(c)).filter(c -> c != null).toList();
		this.repository.saveAll(weather);

		ModelAndView result = new ModelAndView();
		result.setViewName("fragments/welcome");
		result.addObject("_globalSuccessMessage", "acme.default.global.message.success");
		return result;
	}

	protected Weather findWeatherOfCity(final String city) {
		try {
			String apiKey = "2a6d2f4668851aaaa457be9f1ff57775";
			String url = "https://api.openweathermap.org/data/2.5/weather?units=metric" + "&appid=" + apiKey + "&q=" + city;
			RestTemplate api = new RestTemplate();

			ResponseEntity<WeatherPOJO> response = api.getForEntity(url, WeatherPOJO.class);
			Date currentMoment = MomentHelper.getCurrentMoment();
			return Weather.of(response.getBody(), currentMoment, city);
		} catch (final Throwable oops) {
			return null;
		}

	}

}
