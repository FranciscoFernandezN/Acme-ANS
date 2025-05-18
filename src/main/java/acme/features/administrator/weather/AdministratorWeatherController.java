
package acme.features.administrator.weather;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

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
import acme.client.helpers.RandomHelper;
import acme.client.helpers.SpringHelper;
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
		ModelAndView result;
		
		
		return this.doPopulate();
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView doPopulate() {

		List<String> cities = this.repository.findAllCities();
		List<Weather> weather;
		if (SpringHelper.isRunningOn("production"))
			weather = cities.stream().map(c -> this.findWeatherOfCity(c)).filter(c -> c != null).toList();
		else 
			weather = cities.stream().map(c -> this.findWeatherOfCityMocked(c)).filter(c -> c != null).toList();
		
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
	
	protected Weather findWeatherOfCityMocked(final String city) {
		Weather result = new Weather();
		result.setCity(city);
		result.setForecastDate(MomentHelper.getCurrentMoment());
		result.setRainPerHour(RandomHelper.nextDouble(0., 70.));
		result.setSnowPerHour(RandomHelper.nextDouble(0., 10.));
		result.setTemperature(RandomHelper.nextDouble(-40., 60.));
		result.setVisibility(RandomHelper.nextInt(0, 10000));
		result.setWind(RandomHelper.nextDouble(0., 100.));
		return result;
	}

}
