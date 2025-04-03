package acme.features.any.leg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.features.manager.leg.ManagerLegRepository;
import acme.realms.Manager;

@GuiService
public class AnyLegShowService extends AbstractGuiService<Any, Leg>{
	
	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyLegRepository lr;

	// AbstractGuiService interface -------------------------------------------
	
	@Override
	public void authorise() {
		boolean status;
		int legId;
		Leg leg;

		legId = super.getRequest().getData("id", int.class);
		leg = this.lr.findLegById(legId);
		
		super.getResponse().setAuthorised(!leg.getIsDraftMode());
	}
	
	@Override
	public void load() {
		Leg leg;
		int legId;

		legId = super.getRequest().getData("id", int.class);
		leg = this.lr.findLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;
		
		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status");
		
		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIATACode());
		dataset.put("departureAirport", leg.getDepartureAirport().getIATACode());
		dataset.put("aircraft", leg.getAircraft().getRegistrationNumber());
		dataset.put("airline", leg.getAirline().getName());

		super.getResponse().addData(dataset);
	}
	
}
