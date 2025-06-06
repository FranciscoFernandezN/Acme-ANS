
package acme.features.any.flight;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.supportedcurrency.SupportedCurrency;

@SuppressWarnings("deprecation")
@GuiService
public class AnyFlightListBadWeatherService extends AbstractGuiService<Any, Flight> {

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
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		Integer newMonth = currentMoment.getMonth() - 1;
		if (newMonth < 0) {
			currentMoment.setMonth(11);
			currentMoment.setYear(currentMoment.getYear() - 1);
		} else
			currentMoment.setMonth(newMonth);

		flights = this.fr.findAllFlightsPosted();

		super.getBuffer().addData(flights.stream().filter(f -> f.getScheduledArrival().after(currentMoment)).filter(f -> f.getFlownWithBadWeather()).toList());
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
