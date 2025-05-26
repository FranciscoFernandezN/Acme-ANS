
package acme.features.customer.recommendation;

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
		Recommendation rec;
		int recId;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().hasData("id")) {
			recId = super.getRequest().getData("id", int.class);
			rec = this.repository.findRecommendationById(recId);
			status = rec != null;
		} else
			status = false;

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
