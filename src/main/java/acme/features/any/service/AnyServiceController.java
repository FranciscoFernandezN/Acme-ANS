package acme.features.any.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.services.Service;
import acme.features.any.review.AnyReviewCreateService;
import acme.features.any.review.AnyReviewListService;

@GuiController
public class AnyServiceController extends AbstractGuiController<Any, Service> {
	
	// Internal state ---------------------------------------------------------

	@Autowired
	AnyServiceListService	listService;

	@Autowired
	AnyServiceShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
	
}
