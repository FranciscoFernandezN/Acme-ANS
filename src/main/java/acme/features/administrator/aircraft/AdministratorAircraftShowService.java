
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
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

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
		int aircraftId;
		Aircraft aircraft;

		aircraftId = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);

		super.getBuffer().addData(aircraft);
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
