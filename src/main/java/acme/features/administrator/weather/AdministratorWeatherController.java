package acme.features.administrator.weather;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import acme.features.administrator.aircraft.AdministratorAircraftRepository;
@GuiController
public class AdministratorWeatherController  {
	
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorWeatherRepository repository;
	
	// Endpoints --------------------------------------------------------------

	@GetMapping("/administrator/weather/populate")
	public ModelAndView populateInitial() {
		Assert.state(PrincipalHelper.get().hasRealmOfType(Administrator.class), "acme.default.error.not-authorised");

		ModelAndView result = doPopulate();

		

		return result;
	}
	
	// Ancillary methods ------------------------------------------------------

	protected ModelAndView doPopulate() {
		
		List<String> cities = repository.findAllCities();
		List<Weather> weather = cities.stream().map(c -> findWeatherOfCity(c)).filter(c -> c != null).toList();
		repository.saveAll(weather);
		repository.save(findWeatherOfCity("Cambridge Bay"));
		
		ModelAndView result = new ModelAndView();
		result.setViewName("fragments/welcome");
		result.addObject("_globalSuccessMessage", "acme.default.global.message.success");
		return result;
	}
	
	protected Weather findWeatherOfCity(String city) {
		try {
			String apiKey = "2a6d2f4668851aaaa457be9f1ff57775";
			String url = "https://api.openweathermap.org/data/2.5/weather?units=metric" + "&appid=" + apiKey  + "&q=" + city;
			RestTemplate api = new RestTemplate();
			
			ResponseEntity<WeatherPOJO> response = api.getForEntity(url, WeatherPOJO.class);
			Date currentMoment = MomentHelper.getCurrentMoment();
			return Weather.of(response.getBody(), currentMoment, city);
		} catch (final Throwable oops) {
			return null;
		}
		
	}
	
}
