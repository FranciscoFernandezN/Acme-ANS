package acme.features.any.review;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;

import acme.client.components.datatables.DatatableInput;
import acme.client.components.datatables.DatatableOutput;
import acme.client.components.models.Errors;
import acme.client.components.principals.Any;
import acme.client.helpers.DatatablesHelper;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractRestService;
import acme.client.services.RestService;
import acme.entities.reviews.Review;


@RestService
public class AnyReviewFetchService extends AbstractRestService<Any, DatatableOutput<Review>>{
	
	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyReviewRepository rr;

	// AbstractRestService interface -------------------------------------------

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Pair<DatatableInput, Errors> pair;
		DatatableInput input;
		PageRequest pageRequest;
		Errors errors;
		Page<Review> data;
		DatatableOutput<Review> output;

		pair = super.instantiateObject(DatatableInput.class);
		input = pair.getFirst();
		errors = pair.getSecond();
		assert !errors.hasErrors();
		pageRequest = DatatablesHelper.toPageRequest(input);
		
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		currentMoment.setYear(currentMoment.getYear() - 1);

		data = this.rr.fetch(pageRequest, currentMoment);
		output = new DatatableOutput<Review>( //
			input.getDraw(), //
			data.getContent(), //
			data.getTotalElements(), // 
			data.getTotalElements());

		super.getBuffer().addData(output);
	}

	@Override
	public void unbind(final DatatableOutput<Review> review) {
		super.getResponse().addData(review);
	}
	
}
