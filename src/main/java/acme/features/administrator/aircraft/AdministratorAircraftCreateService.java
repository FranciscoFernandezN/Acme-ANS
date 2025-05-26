
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airlines.Airline;

@GuiService
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		Boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (status && super.getRequest().hasData("airline")) {
			int airlineId = super.getRequest().getData("airline", int.class);
			Airline airline = this.repository.findAirlineById(airlineId);
			status = airlineId == 0 || airline != null;
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Aircraft aircraft;

		aircraft = new Aircraft();
		aircraft.setIsEnabled(true);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");

		int airlineId = super.getRequest().getData("airline", int.class);

		Airline airline = this.repository.findAirlineById(airlineId);

		aircraft.setAirline(airline);

	}

	@Override
	public void validate(final Aircraft aircraft) {

		boolean existsThisCode = this.repository.findAllAircrafts().stream().anyMatch(a -> aircraft.getRegistrationNumber().equals(a.getRegistrationNumber()));

		super.state(!existsThisCode, "registrationNumber", "administrator.aircraft.create.already-exists");

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		if (aircraft.getAirline() != null) {
			Airline airline = this.repository.findAirlineById(aircraft.getAirline().getId());
			super.state(airline != null, "name", "administrator.aircraft.error.invalid-airline");
		}
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices statusChoices;
		SelectChoices airlineChoices;

		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		Collection<Airline> airlines = this.repository.findAllAirlines();
		airlineChoices = SelectChoices.from(airlines, "name", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "isEnabled");
		dataset.put("statuses", statusChoices);
		dataset.put("airlines", airlineChoices);

		super.getResponse().addData(dataset);
	}

}
