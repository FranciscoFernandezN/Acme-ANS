
package acme.features.manager.flight;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository fr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.fr.findFlightById(flightId);
		status = flight != null  && super.getRequest().getPrincipal().getRealmOfType(Manager.class).getId() == flight.getManager().getId() && flight.getIsDraftMode();
		
		super.getResponse().setAuthorised(status);
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
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "needsSelfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		super.state(fr.findAllLegsByFlightId(flight.getId()).isEmpty(), "*", "manager.flight.delete.has-legs");
		super.state(flight.getIsDraftMode(), "isDraftMode", "manager.flight.delete.is-published");
	}

	@Override
	public void perform(final Flight flight) {
		this.fr.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		String origin = flight.getOrigin();
		String destiny = flight.getDestiny();

		Date scheduledDeparture = flight.getScheduledDeparture();
		Date scheduledArrival = flight.getScheduledArrival();

		dataset = super.unbindObject(flight, "id", "tag", "cost", "description", "isDraftMode", "needsSelfTransfer");

		dataset.put("origin", origin);
		dataset.put("destiny", destiny);
		dataset.put("scheduledDeparture", scheduledDeparture);
		dataset.put("scheduledArrival", scheduledArrival);
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());
		super.getResponse().addData(dataset);
	}

}
