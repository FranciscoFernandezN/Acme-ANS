
package acme.features.administrator.recommendation;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.recommendations.Recommendation;

@GuiService
public class AdministratorRecommendationListService extends AbstractGuiService<Administrator, Recommendation> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorRecommendationRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Collection<Recommendation> recommendation;

		recommendation = this.repository.findAllRecommendation();

		super.getBuffer().addData(recommendation);
	}

	@Override
	public void unbind(final Recommendation recommendation) {
		Dataset dataset;

		dataset = super.unbindObject(recommendation, "city", "name", "rating", "photoReference");

		dataset.put("openNow", recommendation.getOpenNow() ? "✓" : "x");

		super.getResponse().addData(dataset);
	}

}
