package acme.features.authenticated.manager;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractController;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.features.authenticated.consumer.AuthenticatedConsumerCreateService;
import acme.features.authenticated.consumer.AuthenticatedConsumerUpdateService;
import acme.realms.Manager;

@GuiController
public class AuthenticatedManagerController extends AbstractGuiController<Authenticated, Manager> {
	
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedManagerUpdateService	updateService;
	
	@Autowired
	private AuthenticatedManagerCreateService	createService;


	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
	}
	
}
