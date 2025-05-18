
package acme.features.administrator.ban;

import java.util.Collection;
import java.util.List;

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
public class AdministratorBanCreateService extends AbstractGuiService<Administrator, Ban> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBanRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (status && super.getRequest().hasData("passenger")) {
			List<Passenger> passengers = this.repository.findAllPassengers();
			passengers.removeAll(this.repository.findBannedPassengers(MomentHelper.getCurrentMoment()));
			int passengerId = super.getRequest().getData("passenger", int.class);
			status = passengerId == 0 || passengers.contains(this.repository.findPassengerById(passengerId));
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Ban ban;

		ban = new Ban();
		ban.setBanIssuedDate(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(ban);
	}

	@Override
	public void bind(final Ban ban) {
		super.bindObject(ban, "reasonForBan", "liftDate", "nationality");
		ban.setPassenger(this.repository.findPassengerById(super.getRequest().getData("passenger", int.class)));
	}

	@Override
	public void validate(final Ban ban) {

		boolean confirmation = super.getRequest().getData("confirm", boolean.class);
		super.state(confirmation, "confirm", "acme.validation.confirmation.message");

		if (ban.getLiftDate() != null)
			super.state(ban.getBanIssuedDate().before(ban.getLiftDate()), "liftDate", "administrator.ban.create.incompatibleDates");

		super.state(!this.repository.findBannedPassengersExceptBan(MomentHelper.getCurrentMoment(), ban.getId()).contains(ban.getPassenger()), "banIssuedDate", "administrator.ban.create.stillBanned");

	}

	@Override
	public void perform(final Ban ban) {
		this.repository.save(ban);

	}

	@Override
	public void unbind(final Ban ban) {
		Dataset dataset;
		Passenger passenger = ban.getPassenger();
		int passengerId = passenger != null ? passenger.getId() : -1;

		Collection<Passenger> passengers = this.repository.findAllPassengers();
		passengers.removeAll(this.repository.findBannedPassengers(MomentHelper.getCurrentMoment()));

		SelectChoices passengerChoices = new SelectChoices();
		int passengerIdd = super.getRequest().hasData("passenger") ? super.getRequest().getData("passenger", int.class) : passengerId;

		passengers.stream().distinct().forEach(p -> passengerChoices.add(String.valueOf((Integer) p.getId()), String.format("%s - %s", p.getPassportNumber(), p.getFullName()), passengerIdd == p.getId()));
		passengerChoices.add("0", "----", passengerId <= 0);

		dataset = super.unbindObject(ban, "reasonForBan", "banIssuedDate", "liftDate", "nationality");
		dataset.put("passenger", passengerId);
		dataset.put("passengerChoices", passengerChoices);
		dataset.put("confirm", false);

		super.getResponse().addData(dataset);
	}

}
