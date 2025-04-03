
package acme.features.assistanceAgent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claims.Claim;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimCompleteListService	completeListService;

	@Autowired
	private AssistanceAgentClaimInProgressListService	inProgressListService;

	@Autowired
	private AssistanceAgentClaimShowService			showService;

	@Autowired
	private AssistanceAgentClaimUpdateService			updateService;

	@Autowired
	private AssistanceAgentClaimCreateService			createService;

	@Autowired
	private AssistanceAgentClaimDeleteService			deleteService;

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
