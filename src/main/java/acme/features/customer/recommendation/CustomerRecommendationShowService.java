
package acme.features.customer.recommendation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.recommendations.Recommendation;
import acme.realms.Customer;

@GuiService
public class CustomerRecommendationShowService extends AbstractGuiService<Customer, Recommendation> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerRecommendationRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status) {

			Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			List<String> cities = this.repository.findBookingsByCustomerId(customerId).stream().map(b -> b.getFlight().getDestinyAirport().getCity()).distinct().toList();

			Integer recommendationId = super.getRequest().getData("id", int.class);
			Recommendation recommendation = this.repository.findRecommendationById(recommendationId);

			status = cities.contains(recommendation.getCity());

		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Recommendation recommendation;
		int recommendationId;

		recommendationId = super.getRequest().getData("id", int.class);
		recommendation = this.repository.findRecommendationById(recommendationId);

		super.getBuffer().addData(recommendation);
	}

	@Override
	public void unbind(final Recommendation recommendation) {
		Dataset dataset;

		dataset = super.unbindObject(recommendation, "name", "city", "businessStatus", "formattedAddress", "rating", "userRatingsTotal", "openNow", "photoReference");

		super.getResponse().addData(dataset);
	}

}
