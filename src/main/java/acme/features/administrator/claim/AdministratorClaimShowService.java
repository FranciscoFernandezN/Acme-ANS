
package acme.features.administrator.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;

@GuiService
public class AdministratorClaimShowService extends AbstractGuiService<Administrator, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorClaimRepository acr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.acr.findClaimById(claimId);
		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && claim.getIsPublished();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int claimId;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.acr.findClaimById(claimId);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		boolean siono = this.acr.findLogByClaimId(claim.getId()) != null;

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "claimType", "indicator", "isPublished", "leg");
		dataset.put("claimType", claim.getClaimType());
		dataset.put("indicator", claim.getIndicator());
		dataset.put("leg", claim.getLeg().getUniqueIdentifier());
		dataset.put("hasLog", siono);

		super.getResponse().addData(dataset);
	}
}
