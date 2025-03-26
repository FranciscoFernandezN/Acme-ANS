
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;

@GuiService
public class AdministratorAircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		Integer id;

		// Aseguramos que el id no sea null al obtenerlo de la request
		id = super.getRequest().getData("id", Integer.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		Boolean confirmation = super.getRequest().getData("confirmation", Boolean.class);

		// Verificamos si la confirmación fue marcada como "true"
		super.state(confirmation != null && confirmation, "confirmation", "acme.validation.confirmation.message");

		// Validaciones de campos obligatorios
		super.state(aircraft.getModel() != null && !aircraft.getModel().isEmpty(), "model", "acme.validation.model.required");
		super.state(aircraft.getRegistrationNumber() != null && !aircraft.getRegistrationNumber().isEmpty(), "registrationNumber", "acme.validation.registrationNumber.required");
		super.state(aircraft.getCapacity() != null, "capacity", "acme.validation.capacity.required");
		super.state(aircraft.getCargoWeight() != null, "cargoWeight", "acme.validation.cargoWeight.required");
		super.state(aircraft.getStatus() != null, "status", "acme.validation.status.required");
		super.state(aircraft.getDetails() != null && !aircraft.getDetails().isEmpty(), "details", "acme.validation.details.required");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		// Unbind de todos los campos necesarios
		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("readonly", false);
		dataset.put("statuses", choices);

		super.getResponse().addData(dataset);
	}
}
