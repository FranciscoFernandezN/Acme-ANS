
package acme.features.any.leg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.features.any.flight.AnyFlightRepository;

@GuiService
public class AnyLegListService extends AbstractGuiService<Any, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyLegRepository	lr;

	// AbstractGuiService interface -------------------------------------------

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightRepository	fr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("masterId", int.class);
		flight = this.fr.findFlightById(flightId);

		super.getResponse().setAuthorised(!flight.getIsDraftMode());
	}

	@Override
	public void load() {
		List<Leg> legs;
		int flightId;

		flightId = super.getRequest().getData("masterId", int.class);
		legs = this.lr.findAllLegsByFlightId(flightId);

		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status");
		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("duration", leg.getDuration());
		super.getResponse().addData(dataset);
	}

}
