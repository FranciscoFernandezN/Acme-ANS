
package acme.features.manager.leg;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

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
	public void bind(final Leg leg) {
		super.bindObject(leg, "uniqueIdentifier", "scheduledDeparture", "scheduledArrival", "status", "isDraftMode");

		Airport departureAirport;
		int departureAirportId;

		Airport arrivalAirport;
		int arrivalAirportId;

		departureAirportId = super.getRequest().getData("departureAirport", int.class);
		departureAirport = this.lr.findAirportById(departureAirportId);

		arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		arrivalAirport = this.lr.findAirportById(arrivalAirportId);

		leg.setArrivalAirport(arrivalAirport);
		leg.setDepartureAirport(departureAirport);

		Aircraft aircraft;
		int aircraftId;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.lr.findAircraftById(aircraftId);

		leg.setAircraft(aircraft);

		Flight flight;
		int flightId;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.lr.findFlightById(flightId);

		leg.setFlight(flight);
	}

	@Override
	public void validate(final Leg leg) {

		//Ver si haría falta validar cosas aunque sean con un SelectChoices
		
		Manager manager;

		manager = (Manager) super.getRequest().getPrincipal().getRealmOfType(Manager.class);
		
		Date currentMoment = MomentHelper.getCurrentMoment();

		List<Leg> legs = this.lr.findAllLegs();
		List<String> legIds = legs.stream().filter(l -> l.getId() != leg.getId()).map(Leg::getUniqueIdentifier).toList();

		if (leg.getUniqueIdentifier() != null)
			super.state(!legIds.contains(leg.getUniqueIdentifier()), "uniqueIdentifier", "manager.leg.create.not-unique-identifier");

		if (leg.getScheduledArrival() != null)
			super.state(leg.getScheduledArrival().after(currentMoment), "scheduledArrival", "manager.leg.create.not-future-date");

		if (leg.getScheduledDeparture() != null)
			super.state(leg.getScheduledDeparture().after(currentMoment), "scheduledDeparture", "manager.leg.create.not-future-date");

		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
			super.state(leg.getScheduledArrival().after(leg.getScheduledDeparture()), "scheduledArrival", "manager.leg.create.not-after-arrival");
			super.state(leg.getDuration() <= 24, "scheduledArrival", "manager.leg.create.too-long-leg");
		}

		int arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		int departureAirportId = super.getRequest().getData("departureAirport", int.class);

		if (arrivalAirportId != 0 && departureAirportId != 0)
			super.state(arrivalAirportId != departureAirportId, "arrivalAirport", "manager.leg.create.not-different-airport");

		

		int aircraftId;
		Aircraft aircraft;
		aircraftId = super.getRequest().getData("aircraft", int.class);
		List<Leg> legsOfAircraft = this.lr.findAllLegsOfAircraftByAircraftId(aircraftId);
		aircraft = this.lr.findAircraftById(aircraftId);
		
		if (aircraftId != 0) {
			super.state(aircraft.getAirline().getId() == manager.getAirlineManaging().getId(), "aircraft", "manager.leg.create.not-your-aircraft");
		}
		
		int flightId;
		flightId = super.getRequest().getData("flight", int.class);
		List<Leg> legsOfFlight = this.lr.findAllLegsByFlightId(flightId);
		List<Flight> flights;
		flights = this.lr.findAllFlightsEditableByManagerId(manager.getId());
		if(flightId != 0) {
			super.state(flights.stream().map(f -> f.getId()).anyMatch(f -> f == flightId), "flight", "manager.leg.create.not-your-flight");
		}
		
		
		Predicate<Leg> legsAreBefore = (final Leg l) -> (leg.getScheduledArrival().before(l.getScheduledDeparture()) && leg.getScheduledDeparture().before(l.getScheduledDeparture()));
		Predicate<Leg> legsAreAfter = (final Leg l) -> (leg.getScheduledArrival().after(l.getScheduledArrival()) && leg.getScheduledDeparture().after(l.getScheduledArrival()));

		Predicate<Leg> hasNotConcurrentLegsPredicate = legsAreBefore.or(legsAreAfter);

		super.state(legsOfAircraft.stream().filter(l -> l.getId() != leg.getId()).allMatch(hasNotConcurrentLegsPredicate), "aircraft", "manager.leg.create.already-in-use-aircraft");

		super.state(legsOfFlight.stream().filter(l -> l.getId() != leg.getId()).allMatch(hasNotConcurrentLegsPredicate), "flight", "manager.leg.create.already-in-use-flight");

	}

	@Override
	public void perform(final Leg leg) {
		this.lr.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {

		if (super.getBuffer().getErrors().hasErrors())
			leg.setIsDraftMode(true);

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
		flights = this.lr.findAllFlightsEditableByManagerId(manager.getId());

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
