
package acme.features.administrator.airline;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.entities.airlines.AirlineType;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int airlineId;
		Airline airline;

		airlineId = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(airlineId);
		status = airline != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int airlineId;
		Airline airline;

		airlineId = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(airlineId);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		Date now;

		now = MomentHelper.getCurrentMoment();
		super.bindObject(airline, "name", "IATACode", "website", "type", "email", "contactNumber");
		airline.setFoundationMoment(now);
	}

	@Override
	public void validate(final Airline airline) {
		Boolean existsThisCode = this.repository.findAllAirlines().stream().anyMatch(a -> airline.getIATACode().equals(a.getIATACode()));

		if (existsThisCode == false) {
			boolean confirmation;
			confirmation = super.getRequest().getData("confirmation", boolean.class);
			super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		} else
			super.state(!existsThisCode, "iATACode", "administrator.airline.create.already-exists");
	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "IATACode", "website", "type", "email", "contactNumber");
		dataset.put("operationalScope", choices);

		super.getResponse().addData(dataset);
	}

}
