/*
 * AnyShoutRestController.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.any.review;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import acme.client.components.datatables.DatatableOutput;
import acme.client.components.principals.Any;
import acme.client.controllers.AbstractRestController;
import acme.entities.reviews.Review;

@RestController
public class AnyReviewRestController extends AbstractRestController<Any, DatatableOutput<Review>> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyReviewFetchService fetchService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("fetch", this.fetchService);
	}

}
