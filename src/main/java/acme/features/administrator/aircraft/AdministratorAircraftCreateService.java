
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
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

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

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");

		// Obtener el ID de la aerolínea desde el request
		int airlineId = super.getRequest().getData("airline", int.class);

		// Buscar la aerolínea en el repositorio
		Airline airline = this.repository.findAirlineById(airlineId);

		// Asignar la aerolínea al avión
		aircraft.setAirline(airline);

	}

	@Override
	public void validate(final Aircraft aircraft) {
		// Validar que el número de matrícula no esté repetido
		boolean existsThisCode = this.repository.findAllAircrafts().stream().anyMatch(a -> aircraft.getRegistrationNumber().equals(a.getRegistrationNumber()));

		// Si la matrícula ya existe, se genera un mensaje de error
		super.state(!existsThisCode, "registrationNumber", "administrator.aircraft.create.already-exists");

		// Validar la confirmación solo si la matrícula es válida (no duplicada)
		//if (!existsThisCode) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		//}

		// Verificar que la aerolínea seleccionada existe
		if (aircraft.getAirline() != null) {
			Airline airline = this.repository.findAirlineById(aircraft.getAirline().getId());
			super.state(airline != null, "iATACode", "administrator.aircraft.error.invalid-airline");
		}
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices statusChoices, airlineChoices;

		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		// Obtener aerolíneas existentes
		Collection<Airline> airlines = this.repository.findAllAirlines();
		airlineChoices = SelectChoices.from(airlines, "iATACode", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("statuses", statusChoices);
		dataset.put("airlines", airlineChoices);

		super.getResponse().addData(dataset);
	}

}
