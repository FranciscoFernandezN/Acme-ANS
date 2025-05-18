
package acme.features.administrator.ban;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passengers.Ban;
import acme.entities.passengers.Passenger;

@GuiService
public class AdministratorBanUpdateService extends AbstractGuiService<Administrator, Ban> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBanRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Ban ban;

		ban = this.repository.findBanById(super.getRequest().getData("id", int.class));

		super.getBuffer().addData(ban);
	}

	@Override
	public void bind(final Ban ban) {
		super.bindObject(ban, "reasonForBan", "liftDate");
	}

	@Override
	public void validate(final Ban ban) {

		boolean confirmation = super.getRequest().getData("confirm", boolean.class);
		super.state(confirmation, "confirm", "acme.validation.confirmation.message");

		if (ban.getLiftDate() != null)
			super.state(ban.getBanIssuedDate().before(ban.getLiftDate()), "liftDate", "administrator.ban.update.incompatibleDates");

		super.state(!this.repository.findBannedPassengersExceptBan(MomentHelper.getCurrentMoment(), ban.getId()).contains(ban.getPassenger()), "banIssuedDate", "administrator.ban.update.stillBanned");

	}

	@Override
	public void perform(final Ban ban) {
		this.repository.save(ban);
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
