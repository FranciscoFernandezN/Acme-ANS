
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airlines.Airline;

@GuiService
public class AdministratorAircraftDisableService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Aircraft aircraft;

		aircraft = new Aircraft();

		aircraft.setIsEnabled(true);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");

		int airlineId = super.getRequest().getData("airline", int.class);

		Airline airline = this.repository.findAirlineById(airlineId);

		aircraft.setAirline(airline);

	}

	@Override
	public void validate(final Aircraft aircraft) {

		// Obtener el avión original antes de la actualización
		Aircraft original = this.repository.findAircraftById(aircraft.getId());

		// Validar que no se intente modificar un avión deshabilitado
		if (original != null && !original.getIsEnabled()) {
			super.state(false, "*", "administrator.aircraft.cannot-modify-disabled");
			return;
		}

		// Validar si el registrationNumber ha cambiado antes de verificar duplicados
		if (original == null || !original.getRegistrationNumber().equals(aircraft.getRegistrationNumber())) {
			boolean existsThisCode = this.repository.findAllAircrafts().stream().anyMatch(a -> aircraft.getRegistrationNumber().equals(a.getRegistrationNumber()));

			super.state(!existsThisCode, "registrationNumber", "administrator.aircraft.create.already-exists");
		}

		// Verificar la confirmación del usuario
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		// Validar que la aerolínea existe
		if (aircraft.getAirline() != null) {
			Airline airline = this.repository.findAirlineById(aircraft.getAirline().getId());
			super.state(airline != null, "name", "administrator.aircraft.error.invalid-airline");
		}
	}

	@Override
	public void perform(final Aircraft aircraft) {
		aircraft.setIsEnabled(false);
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices statusChoices;
		SelectChoices airlinesChoices;

		Collection<Airline> airlines = this.repository.findAllAirlines();
		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		Airline selectedAirline = aircraft.getAirline();
		airlinesChoices = SelectChoices.from(airlines, "name", selectedAirline);

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "isEnabled");
		dataset.put("statuses", statusChoices);
		dataset.put("airlines", airlinesChoices);

		dataset.put("airline", selectedAirline != null ? selectedAirline.getId() : null);

		super.getResponse().addData(dataset);
	}

}
