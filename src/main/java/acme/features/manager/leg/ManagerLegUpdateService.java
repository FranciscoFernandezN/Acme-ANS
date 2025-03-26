
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
		status = super.getRequest().getPrincipal().getRealmOfType(Manager.class).getId() == leg.getManager().getId();

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
	}

	@Override
	public void validate(final Leg leg) {

		Date currentMoment = MomentHelper.getCurrentMoment();

		List<Leg> legs = this.lr.findAllLegs();
		List<String> legIds = legs.stream().filter(l -> l.getId() != leg.getId()).map(Leg::getUniqueIdentifier).toList();

		if (leg.getUniqueIdentifier() != null)
			super.state(!legIds.contains(leg.getUniqueIdentifier()), "uniqueIdentifier", "manager.leg.create.not-unique-identifier");

		if (leg.getScheduledArrival() != null)
			super.state(leg.getScheduledArrival().after(currentMoment), "scheduledArrival", "manager.leg.create.not-future-date");

		if (leg.getScheduledDeparture() != null)
			super.state(leg.getScheduledDeparture().after(currentMoment), "scheduledDeparture", "manager.leg.create.not-future-date");

		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null)
			super.state(leg.getScheduledArrival().after(leg.getScheduledDeparture()), "scheduledArrival", "manager.leg.create.not-after-arrival");

		int arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		int departureAirportId = super.getRequest().getData("departureAirport", int.class);

		if (arrivalAirportId != 0 && departureAirportId != 0)
			super.state(arrivalAirportId != departureAirportId, "arrivalAirport", "manager.leg.create.not-different-airport");

		super.state(leg.getDuration() <= 24, "scheduledArrival", "manager.leg.create.too-long-leg");

		int aircraftId;
		aircraftId = super.getRequest().getData("aircraft", int.class);
		List<Leg> legsOfAircraft = this.lr.findAllLegsOfAircraftByAircraftId(aircraftId);

		Predicate<Leg> hasConcurrenLegsPredicate = (final Leg l) -> !(l.getScheduledArrival().before(leg.getScheduledArrival()) && l.getScheduledDeparture().before(leg.getScheduledDeparture())
			|| l.getScheduledArrival().after(leg.getScheduledArrival()) && l.getScheduledDeparture().after(leg.getScheduledDeparture()));
		super.state(legsOfAircraft.stream().filter(l -> l.getId() != leg.getId()).noneMatch(hasConcurrenLegsPredicate), "aircraft", "manager.leg.create.already-in-use-aircraft");

	}

	@Override
	public void perform(final Leg leg) {
		this.lr.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;
		List<Airport> airports;
		List<Aircraft> aircrafts;

		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		SelectChoices arrivalIATACodeChoices;
		SelectChoices departureIATACodeChoices;
		SelectChoices registrationNumberChoices;
		SelectChoices legStatuses;

		airports = this.lr.findAllAirports();
		aircrafts = this.lr.findAllAircraftsByAirlineId(manager.getAirlineManaging().getId());

		arrivalIATACodeChoices = SelectChoices.from(airports, "iATACode", leg.getArrivalAirport());
		departureIATACodeChoices = SelectChoices.from(airports, "iATACode", leg.getDepartureAirport());
		registrationNumberChoices = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());

		legStatuses = SelectChoices.from(LegStatus.class, leg.getStatus());

		dataset = super.unbindObject(leg, "uniqueIdentifier", "scheduledDeparture", "scheduledArrival", "status", "isDraftMode");
		dataset.put("arrivalIATACodes", arrivalIATACodeChoices);
		dataset.put("departureIATACodes", departureIATACodeChoices);
		dataset.put("registrationNumbers", registrationNumberChoices);
		dataset.put("statuses", legStatuses);
		super.getResponse().addData(dataset);
	}

}
