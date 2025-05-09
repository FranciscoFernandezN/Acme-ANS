
package acme.features.administrator.recommendation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.client.helpers.Assert;
import acme.client.helpers.PrincipalHelper;
import acme.components.recommendation.RecommendationPOJO;
import acme.components.recommendation.ResultsPOJO;
import acme.entities.recommendations.Recommendation;

@GuiController
public class AdministratorRecommendationController extends AbstractGuiController<Administrator, Recommendation> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorRecommendationRepository	repository;

	@Autowired
	private AdministratorRecommendationListService	listService;

	@Autowired
	private AdministratorRecommendationShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

	// Endpoints --------------------------------------------------------------

	@GetMapping("/administrator/recommendation/populate")
	public ModelAndView populateInitial() {
		Assert.state(PrincipalHelper.get().hasRealmOfType(Administrator.class), "acme.default.error.not-authorised");

		return this.doPopulate();
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView doPopulate() {

		List<String> cities = this.repository.findAllCities();

		List<String> recommendationsNames = this.repository.findAllRecommendationsNames();

		List<List<Recommendation>> recommendations = cities.stream().map(c -> this.findRecommendationOfCity(c)).filter(c -> c != null).toList();
		for (List<Recommendation> lis : recommendations)
			for (Recommendation rec : lis) {
				String name = rec.getName();
				if (recommendationsNames.contains(name))
					this.repository.delete(this.repository.findRecommendationByName(name));
				this.repository.save(rec);
			}
		ModelAndView result = new ModelAndView();
		result.setViewName("fragments/welcome");
		result.addObject("_globalSuccessMessage", "acme.default.global.message.success");
		return result;
	}

	protected List<Recommendation> findRecommendationOfCity(final String city) {
		try {
			String formattedCity = city.toLowerCase().replace(" ", "+");
			String apiKey = "AIzaSyC2AU9Q3g-xuKWQiphz4x_meZBn2eKmmTs";
			String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + formattedCity + "+point+of+interest&language=en&key=" + apiKey;
			RestTemplate api = new RestTemplate();
			ResponseEntity<ResultsPOJO> response = api.getForEntity(url, ResultsPOJO.class);
			List<Recommendation> results = new ArrayList<>();
			Recommendation recommendation;
			for (RecommendationPOJO recPOJO : response.getBody().getRecommendations().subList(0, 5)) {
				recommendation = Recommendation.of(recPOJO, city);
				if (recommendation != null)
					results.add(recommendation);
			}
			return results;
		} catch (final Throwable oops) {
			return null;
		}

	}

}
