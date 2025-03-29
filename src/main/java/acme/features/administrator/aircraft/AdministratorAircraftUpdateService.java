
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
		int id;

		// Aseguramos que el id no sea null al obtenerlo de la request
		id = super.getRequest().getData("id", Integer.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		// Validar que el número de matrícula no esté repetido
		//boolean existsThisCode = this.repository.findAllAircrafts().stream().anyMatch(a -> aircraft.getRegistrationNumber().equals(a.getRegistrationNumber()));

		// Si la matrícula ya existe, se genera un mensaje de error
		//super.state(!existsThisCode, "registrationNumber", "administrator.aircraft.create.already-exists");

		// Validar la confirmación solo si la matrícula es válida (no duplicada)
		//if (!existsThisCode) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		//}

		// Verificar que la aerolínea seleccionada existe
		if (aircraft.getAirline() != null) {
			Airline airline = this.repository.findAirlineById(aircraft.getAirline().getId());
			super.state(airline != null, "airline", "administrator.aircraft.error.invalid-airline");
		}
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices statusChoices;
		SelectChoices airlinesChoices;

		Collection<Airline> airlines = this.repository.findAllAirlines();
		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		// Verificar si la aerolínea no es nula antes de pasarla a SelectChoices
		Airline selectedAirline = aircraft.getAirline() != null ? aircraft.getAirline() : new Airline();
		airlinesChoices = SelectChoices.from(airlines, "name", selectedAirline);

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("statuses", statusChoices);
		dataset.put("airlines", airlinesChoices);

		// Si necesitas enviar explícitamente la aerolínea seleccionada en el dataset:
		dataset.put("airline", aircraft.getAirline() != null ? aircraft.getAirline().getId() : null);

		super.getResponse().addData(dataset);
	}
}
