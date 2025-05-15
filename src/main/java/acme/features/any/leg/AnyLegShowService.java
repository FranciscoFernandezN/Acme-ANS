
package acme.features.any.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;

@GuiService
public class AnyLegShowService extends AbstractGuiService<Any, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyLegRepository lr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
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

		dataset = super.unbindObject(leg, "uniqueIdentifier", "scheduledDeparture", "scheduledArrival", "status");
		
		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIATACode());
		dataset.put("departureAirport", leg.getDepartureAirport().getIATACode());
		dataset.put("aircraft", leg.getAircraft().getRegistrationNumber());
		dataset.put("airline", leg.getAirline().getName());

		super.getResponse().addData(dataset);
	}

}
