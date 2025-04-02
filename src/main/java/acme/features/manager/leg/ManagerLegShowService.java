
package acme.features.manager.leg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository lr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int legId;
		Leg leg;

		legId = super.getRequest().getData("id", int.class);
		leg = this.lr.findLegById(legId);
		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class) && super.getRequest().getPrincipal().getRealmOfType(Manager.class).getId() == leg.getManager().getId();

		super.getResponse().setAuthorised(status);
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
		List<Airport> airports;
		List<Aircraft> aircrafts;
		List<Flight> flights;

		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		SelectChoices arrivalIATACodeChoices;
		SelectChoices departureIATACodeChoices;
		SelectChoices registrationNumberChoices;
		SelectChoices flightIdChoices;
		SelectChoices legStatuses;

		airports = this.lr.findAllAirports();
		aircrafts = this.lr.findAllAircraftsByAirlineId(manager.getAirlineManaging().getId());
		if (leg.getIsDraftMode())
			flights = this.lr.findAllFlightsEditableByManagerId(manager.getId());
		else
			flights = this.lr.findAllFlightsByManagerId(manager.getId());

		arrivalIATACodeChoices = SelectChoices.from(airports, "iATACode", leg.getArrivalAirport());
		departureIATACodeChoices = SelectChoices.from(airports, "iATACode", leg.getDepartureAirport());
		registrationNumberChoices = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());
		flightIdChoices = SelectChoices.from(flights, "id", leg.getFlight());

		legStatuses = SelectChoices.from(LegStatus.class, leg.getStatus());

		dataset = super.unbindObject(leg, "uniqueIdentifier", "scheduledDeparture", "scheduledArrival", "status", "isDraftMode");
		dataset.put("arrivalIATACodes", arrivalIATACodeChoices);
		dataset.put("departureIATACodes", departureIATACodeChoices);
		dataset.put("registrationNumbers", registrationNumberChoices);
		dataset.put("statuses", legStatuses);
		dataset.put("flightChoices", flightIdChoices);
		super.getResponse().addData(dataset);
	}

}
