
package acme.features.assistanceAgents.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claims.Claim;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentsClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentsClaimCompleteListService	completeListService;

	@Autowired
	private AssistanceAgentsClaimInProgressListService	inProgressListService;

	@Autowired
	private AssistanceAgentsClaimShowService			showService;

	@Autowired
	private AssistanceAgentsClaimUpdateService			updateService;

	@Autowired
	private AssistanceAgentsClaimCreateService			createService;

	@Autowired
	private AssistanceAgentsClaimDeleteService			deleteService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-complete", "list", this.completeListService);
		super.addCustomCommand("list-in-progress", "list", this.inProgressListService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
	}
}
