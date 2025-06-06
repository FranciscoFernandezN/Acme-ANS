
package acme.features.any.flight;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiService
public class AnyFlightListService extends AbstractGuiService<Any, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightRepository fr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		List<Flight> flights;

		flights = this.fr.findAllFlightsPosted();

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		String origin = flight.getOrigin();
		String destiny = flight.getDestiny();

		Date scheduledDeparture = flight.getScheduledDeparture();
		Date scheduledArrival = flight.getScheduledArrival();

		dataset = super.unbindObject(flight, "tag", "cost");

		dataset.put("needsSelfTransfer", flight.getNeedsSelfTransfer() ? "✓" : "x");

		dataset.put("defaultCost", SupportedCurrency.convertToDefault(flight.getCost()));

		dataset.put("origin", origin);
		dataset.put("destiny", destiny);
		dataset.put("scheduledDeparture", scheduledDeparture);
		dataset.put("scheduledArrival", scheduledArrival);

		super.getResponse().addData(dataset);
	}

}
