
package acme.features.manager.flight;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightCreateService extends AbstractGuiService<Manager, Flight> {

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
		Flight flight = new Flight();
		Manager manager;

		manager = (Manager) super.getRequest().getPrincipal().getRealmOfType(Manager.class);
		flight.setManager(manager);
		flight.setIsDraftMode(true);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "needsSelfTransfer", "cost", "description", "isDraftMode");
	}

	@Override
	public void validate(final Flight flight) {
		List<Leg> legs = this.fr.findAllLegsByFlightId(flight.getId());
		super.state(flight.getIsDraftMode() || !flight.getIsDraftMode() && !legs.isEmpty() && legs.stream().allMatch(l -> !l.getIsDraftMode()), "isDraftMode", "manager.flight.create.cant-be-published");
	}

	@Override
	public void perform(final Flight flight) {
		this.fr.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {

		if (super.getBuffer().getErrors().hasErrors())
			flight.setIsDraftMode(true);

		Dataset dataset;

		String origin = flight.getOrigin();
		String destiny = flight.getDestiny();

		Date scheduledDeparture = flight.getScheduledDeparture();
		Date scheduledArrival = flight.getScheduledArrival();

		dataset = super.unbindObject(flight, "id", "tag", "cost", "description","isDraftMode", "needsSelfTransfer");

		dataset.put("origin", origin == null ? "N/A" : origin);
		dataset.put("destiny", destiny == null ? "N/A" : destiny);
		dataset.put("scheduledDeparture", scheduledDeparture == null ? "N/A" : scheduledDeparture);
		dataset.put("scheduledArrival", scheduledArrival == null ? "N/A" : scheduledArrival);
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());
		super.getResponse().addData(dataset);
	}

}
