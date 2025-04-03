
package acme.features.administrator.airline;

import java.util.Date;
import java.util.List;

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
	private AdministratorAirlineRepository ar;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Airline airline;
		int airlineId;

		airlineId = super.getRequest().getData("id", int.class);
		airline = this.ar.findAirlineById(airlineId);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iATACode", "website", "airlineType", "foundationMoment", "email", "contactNumber");
	}

	@Override
	public void validate(final Airline airline) {
		List<Airline> airlines = this.ar.findAllAirlines();
		List<String> airlineIds = airlines.stream().filter(a -> a.getId() != airline.getId()).map(Airline::getIATACode).toList();

		if (airline.getIATACode() != null)
			super.state(!airlineIds.contains(airline.getIATACode()), "iATACode", "administrator.airline.create.not-unique-iata");
		
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		
	}

	@Override
	public void perform(final Airline airline) {
		this.ar.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(AirlineType.class, airline.getAirlineType());

		dataset = super.unbindObject(airline, "name", "iATACode", "website", "foundationMoment", "email", "contactNumber");
		dataset.put("airlineTypes", choices);

		super.getResponse().addData(dataset);
	}

}
