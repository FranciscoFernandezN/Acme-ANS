
package acme.features.any.review;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.reviews.Review;

@GuiService
public class AnyReviewCreateService extends AbstractGuiService<Any, Review> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyReviewRepository rr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Review review;

		review = new Review();

		super.getBuffer().addData(review);
	}

	@Override
	public void bind(final Review review) {
		super.bindObject(review, "name", "subject", "body", "score", "isRecommended");
		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		review.setPostedAt(currentMoment);
	}

	@Override
	public void validate(final Review review) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Review review) {
		this.rr.save(review);
	}

	@Override
	public void unbind(final Review review) {
		Dataset dataset;

		dataset = super.unbindObject(review, "name", "subject", "body", "score", "isRecommended", "postedAt");
		super.getResponse().addData(dataset);
	}

}
