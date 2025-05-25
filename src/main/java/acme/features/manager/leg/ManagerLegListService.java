
package acme.features.manager.leg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
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
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Manager.class));
	}

	@Override
	public void load() {
		List<Leg> legs;
		int managerId;

		managerId = super.getRequest().getPrincipal().getRealmOfType(Manager.class).getId();
		legs = this.lr.findAllLegsByManagerId(managerId);

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
