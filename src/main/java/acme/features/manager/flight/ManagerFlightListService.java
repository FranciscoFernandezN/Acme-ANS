
package acme.features.manager.flight;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository fr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Manager.class));
	}

	@Override
	public void load() {
		List<Flight> flights;
		int managerId;

		managerId = super.getRequest().getPrincipal().getRealmOfType(Manager.class).getId();
		flights = this.fr.findAllFlightsByManagerId(managerId);

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		String origin = flight.getOrigin();
		String destiny = flight.getDestiny();

		Date scheduledDeparture = flight.getScheduledDeparture();
		Date scheduledArrival = flight.getScheduledArrival();

		dataset = super.unbindObject(flight, "id", "tag", "cost", "isDraftMode", "needsSelfTransfer");

		dataset.put("origin", origin == null ? "N/A" : origin);
		dataset.put("destiny", destiny == null ? "N/A" : destiny);
		dataset.put("scheduledDeparture", scheduledDeparture == null ? "N/A" : scheduledDeparture);
		dataset.put("scheduledArrival", scheduledArrival == null ? "N/A" : scheduledArrival);

		super.getResponse().addData(dataset);
	}

}
