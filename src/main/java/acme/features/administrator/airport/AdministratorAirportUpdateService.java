
package acme.features.administrator.airport;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.airports.OperationalScope;

@GuiService
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirportRepository aar;

	// AbstractGuiService interfaced ------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Airport airport;
		int id;

		id = super.getRequest().getData("id", int.class);
		airport = this.aar.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iATACode", "operationalScope", "city", "country", "website", "email", "contactNumber", "confirm");
	}

	@Override
	public void validate(final Airport object) {

		List<Airport> airports = this.aar.findAllAirports();
		List<String> airportCodes = airports.stream().filter(m -> m.getId() != object.getId()).map(Airport::getIATACode).toList();

		boolean confirmation = super.getRequest().getData("confirm", boolean.class);
		super.state(confirmation, "confirm", "acme.validation.confirmation.message");

		super.state(!airportCodes.contains(object.getIATACode()), "iATACode", "administrator.airport.create.already-exists");

	}

	@Override
	public void perform(final Airport airport) {
		this.aar.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(OperationalScope.class, airport.getOperationalScope());

		dataset = super.unbindObject(airport, "name", "iATACode", "operationalScope", "city", "country", "website", "email", "contactNumber");
		dataset.put("operationalScope", choices);
		dataset.put("confirm", false);

		super.getResponse().addData(dataset);
	}
}
