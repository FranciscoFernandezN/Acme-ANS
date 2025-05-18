
package acme.features.administrator.ban;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passengers.Ban;

@GuiService
public class AdministratorBanListService extends AbstractGuiService<Administrator, Ban> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBanRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Collection<Ban> bans;
		Integer passengerId;

		passengerId = super.getRequest().getData("passengerId", int.class);
		bans = this.repository.findBanOfPassenger(passengerId);

		super.getBuffer().addData(bans);
	}

	@Override
	public void unbind(final Ban bans) {
		Dataset dataset;

		dataset = super.unbindObject(bans, "reasonForBan", "banIssuedDate", "liftDate");

		super.getResponse().addData(dataset);
	}

}
