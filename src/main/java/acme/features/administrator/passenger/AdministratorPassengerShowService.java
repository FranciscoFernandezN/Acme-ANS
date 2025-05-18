
package acme.features.administrator.passenger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passengers.Passenger;

@GuiService
public class AdministratorPassengerShowService extends AbstractGuiService<Administrator, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int passengerId;

		passengerId = super.getRequest().getData("id", int.class);
		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && !this.repository.findPublishedBookingsOfPassenger(passengerId).isEmpty();

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Passenger passenger;
		int passengerId;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		String specialNeeds = passenger.getSpecialNeeds();

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth");
		dataset.put("specialNeeds", specialNeeds.isBlank() ? "N/A" : specialNeeds);

		List<String> nationalities = this.repository.findNationality(passenger.getId());
		if (!nationalities.isEmpty())
			dataset.put("nationality", nationalities.get(0));

		super.getResponse().addData(dataset);
	}

}
