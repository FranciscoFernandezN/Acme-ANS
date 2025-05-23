
package acme.features.any.review;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.reviews.Review;

@SuppressWarnings("deprecation")
@GuiService
public class AnyReviewListService extends AbstractGuiService<Any, Review> {

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
		List<Review> reviews;
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		currentMoment.setYear(currentMoment.getYear() - 1);
		reviews = this.rr.findReviewsNewerThanDate(currentMoment);

		super.getBuffer().addData(reviews);
	}

	@Override
	public void unbind(final Review review) {
		Dataset dataset;

		dataset = super.unbindObject(review, "name", "postedAt", "subject", "body", "score");

		dataset.put("isRecommended", review.getIsRecommended() ? "✓" : "x");

		super.getResponse().addData(dataset);
	}

}
