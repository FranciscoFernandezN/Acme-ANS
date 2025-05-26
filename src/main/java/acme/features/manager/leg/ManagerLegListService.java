
package acme.features.manager.leg;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository lr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId;
		Flight flight;
		boolean status;
		
		
		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);
		if(status) {
			flightId = super.getRequest().getData("masterId", int.class, 0);
			if(flightId != 0) {
				flight = this.lr.findFlightById(flightId);
				Manager manager = (Manager) super.getRequest().getPrincipal().getRealmOfType(Manager.class);
				status = flight != null && flight.getManager().getId() == manager.getId();
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		List<Leg> legs;
		int managerId;
		int flightId;

		managerId = super.getRequest().getPrincipal().getRealmOfType(Manager.class).getId();
		flightId = super.getRequest().getData("masterId", int.class, 0);
		if(flightId == 0)
			legs = this.lr.findAllLegsByManagerId(managerId);
		else 
			legs = this.lr.findAllLegsByFlightId(flightId);

		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status");
		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("isDraftMode", leg.getIsDraftMode() ? "✓" : "x");
		dataset.put("duration", leg.getDuration());
		dataset.put("flight", leg.getFlight().getId());
		super.getResponse().addData(dataset);
	}

}
