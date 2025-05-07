
package acme.features.any.recommendation;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.recommendations.Recommendation;

@GuiController
public class AnyRecommendationController extends AbstractGuiController<Any, Recommendation> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyRecommendationListService	listService;

	@Autowired
	private AnyRecommendationShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
}
