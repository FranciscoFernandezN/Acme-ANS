
package acme.features.manager.leg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository lr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("id", int.class);
		leg = this.lr.findLegById(legId);
		
		Boolean status = leg != null && super.getRequest().getPrincipal().getRealmOfType(Manager.class).getId() == leg.getManager().getId() && leg.getIsDraftMode();
		
		if(status) {
			Manager manager = (Manager) super.getRequest().getPrincipal().getRealmOfType(Manager.class);
			Integer aircraftId = super.getRequest().getData("aircraft", int.class, 0);
			Integer arrivalId = super.getRequest().getData("arrivalAirport", int.class, 0);
			Integer departureId = super.getRequest().getData("departureAirport", int.class, 0);
			Integer flightId = super.getRequest().getData("flight", int.class, 0);
			List<Integer> airports = this.lr.findAllAirports().stream().map(a -> a.getId()).toList();
			List<Integer> aircrafts = this.lr.findAllAircraftsByAirlineId(manager.getAirlineManaging().getId()).stream().map(a -> a.getId()).toList();
			List<Integer> flights = this.lr.findAllFlightsEditableByManagerId(manager.getId()).stream().map(f -> f.getId()).toList();
		}

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
		super.bindObject(leg, "uniqueIdentifier", "scheduledDeparture", "scheduledArrival", "status");
	}

	@Override
	public void validate(final Leg leg) {
		super.state(leg.getIsDraftMode(), "isDraftMode", "manager.leg.delete.is-published");
	}

	@Override
	public void perform(final Leg leg) {
		this.lr.delete(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "uniqueIdentifier", "scheduledDeparture", "scheduledArrival", "status", "isDraftMode");
		super.getResponse().addData(dataset);
	}

}
