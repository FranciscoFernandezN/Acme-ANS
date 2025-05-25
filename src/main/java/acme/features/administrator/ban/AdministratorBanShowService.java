
package acme.features.administrator.ban;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passengers.Ban;
import acme.entities.passengers.Passenger;

@GuiService
public class AdministratorBanShowService extends AbstractGuiService<Administrator, Ban> {

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
		Ban ban;
		int banId;

		banId = super.getRequest().getData("id", int.class);
		ban = this.repository.findBanById(banId);

		super.getBuffer().addData(ban);
	}

	@Override
	public void unbind(final Ban ban) {
		Dataset dataset;
		Passenger passenger = ban.getPassenger();
		int passengerId = passenger.getId();
		SelectChoices passengerChoices = new SelectChoices();
		passengerChoices.add(String.valueOf((Integer) passengerId), String.format("%s - %s", passenger.getPassportNumber(), passenger.getFullName()), true);

		dataset = super.unbindObject(ban, "reasonForBan", "banIssuedDate", "liftDate");
		dataset.put("passenger", passengerId);
		dataset.put("passengerChoices", passengerChoices);
		dataset.put("confirm", false);

		super.getResponse().addData(dataset);
	}

}
