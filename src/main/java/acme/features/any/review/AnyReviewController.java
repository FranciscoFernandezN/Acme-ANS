
package acme.features.any.review;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.reviews.Review;

@GuiController
public class AnyReviewController extends AbstractGuiController<Any, Review> {
	// Internal state ---------------------------------------------------------

	@Autowired
	AnyReviewListService	listService;

	@Autowired
	AnyReviewCreateService	createService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("create", this.createService);
	}
}
