package acme.features.any.flight;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.AbstractService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.features.manager.flight.ManagerFlightRepository;
import acme.realms.Manager;

@GuiService
public class AnyFlightShowService extends AbstractGuiService<Any, Flight> {
	
	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightRepository fr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.fr.findFlightById(flightId);

		super.getResponse().setAuthorised(!flight.getIsDraftMode());
	}

	@Override
	public void load() {
		Flight flight;
		int flightId;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.fr.findFlightById(flightId);

		super.getBuffer().addData(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		String origin = flight.getOrigin();
		String destiny = flight.getDestiny();

		Date scheduledDeparture = flight.getScheduledDeparture();
		Date scheduledArrival = flight.getScheduledArrival();

		dataset = super.unbindObject(flight, "id", "tag", "cost", "description", "needsSelfTransfer");

		dataset.put("origin", origin);
		dataset.put("destiny", destiny);
		dataset.put("scheduledDeparture", scheduledDeparture);
		dataset.put("scheduledArrival", scheduledArrival);
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());
		
		
		List<Leg> legsOfFlight = this.fr.findAllLegsByFlightId(flight.getId());
		
		Leg firstLeg = legsOfFlight.remove(0);
		String legsInfo = firstLeg.getFlightNumber();
		
		for(Leg l: legsOfFlight) {
			legsInfo = legsInfo + " -> " + l.getFlightNumber();
		}
		
		dataset.put("legs", legsInfo);
		
		super.getResponse().addData(dataset);
	}
	
}
