
package acme.features.administrator.passenger;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passengers.Passenger;

@GuiService
public class AdministratorLiftedBanPassengersListService extends AbstractGuiService<Administrator, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;

		Date today = MomentHelper.getCurrentMoment();
		passengers = this.repository.findPassengersEverBanned();
		passengers.removeAll(this.repository.findBannedPassengers(today));

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passengers) {
		Dataset dataset;
		String specialNeeds = passengers.getSpecialNeeds();
		String nationality = this.repository.findNationality(passengers.getId()).get(0);

		dataset = super.unbindObject(passengers, "fullName", "email", "passportNumber", "dateOfBirth");
		dataset.put("specialNeeds", specialNeeds.isBlank() ? "N/A" : specialNeeds);
		dataset.put("nationality", nationality);

		super.getResponse().addData(dataset);
		super.getResponse().addGlobal("banned", true);

	}

}
