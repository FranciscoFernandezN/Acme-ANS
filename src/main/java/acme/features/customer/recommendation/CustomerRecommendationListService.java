
package acme.features.customer.recommendation;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.recommendations.Recommendation;
import acme.realms.Customer;

@GuiService
public class CustomerRecommendationListService extends AbstractGuiService<Customer, Recommendation> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerRecommendationRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (status && super.getRequest().hasData("city")) {
			String city = super.getRequest().getData("city", String.class);
			Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			List<String> cities = this.repository.findBookingsByCustomerId(customerId).stream().map(b -> b.getFlight().getDestinyAirport().getCity()).distinct().toList();
			status = cities.contains(city);
		}
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Collection<Recommendation> recommendation;

		if (super.getRequest().hasData("city"))
			recommendation = this.repository.findRecommendationsByCity(super.getRequest().getData("city", String.class));
		else {
			Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			List<String> cities = this.repository.findBookingsByCustomerId(customerId).stream().map(b -> b.getFlight().getDestinyAirport().getCity()).distinct().toList();
			recommendation = this.repository.findRecommendationsByCities(cities);
		}

		super.getBuffer().addData(recommendation);
	}

	@Override
	public void unbind(final Recommendation recommendation) {
		Dataset dataset;

		dataset = super.unbindObject(recommendation, "city", "name", "rating", "openNow", "photoReference");

		super.getResponse().addData(dataset);
	}

}
