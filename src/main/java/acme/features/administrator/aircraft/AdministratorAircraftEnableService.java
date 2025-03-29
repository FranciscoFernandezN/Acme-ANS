
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;

@GuiService
public class AdministratorAircraftEnableService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		boolean isAdmin = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(isAdmin);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", Integer.class);
		Aircraft aircraft = this.repository.findAircraftById(id);

		// Validar que la aeronave exista antes de continuar
		super.state(aircraft != null, "id", "administrator.aircraft.error.not-found");

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "enabled");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		aircraft.setEnabled(true);
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset = super.unbindObject(aircraft, "enabled");
		super.getResponse().addData(dataset);
	}
}
