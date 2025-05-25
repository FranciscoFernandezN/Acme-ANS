
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
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
		super.bindObject(flight, "tag", "needsSelfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
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

		dataset = super.unbindObject(flight, "id", "tag", "cost", "description", "isDraftMode", "needsSelfTransfer");

		dataset.put("origin", "N/A");
		dataset.put("destiny", "N/A");
		dataset.put("scheduledDeparture", "N/A");
		dataset.put("scheduledArrival", "N/A");

		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());
		super.getResponse().addData(dataset);
	}

}
