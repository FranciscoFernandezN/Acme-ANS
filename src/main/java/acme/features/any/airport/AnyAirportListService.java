
package acme.features.any.airport;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiService
public class AnyAirportListService extends AbstractGuiService<Any, Airport> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyAirportRepository aar;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		List<Airport> airports;

		airports = this.aar.findAllAirports();

		super.getBuffer().addData(airports);
	}

	@Override
	public void unbind(final Airport airport) {
		Dataset dataset;

		dataset = super.unbindObject(airport, "name", "iATACode", "operationalScope", "city", "country", "website", "email", "contactNumber");

		super.getResponse().addData(dataset);
	}

}
