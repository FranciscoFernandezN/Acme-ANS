
package acme.features.administrator.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.services.Service;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiService
public class AdministratorServiceShowService extends AbstractGuiService<Administrator, Service> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorServiceRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Service service;
		int serviceId;

		serviceId = super.getRequest().getData("id", int.class);
		service = this.repository.findServiceById(serviceId);

		super.getBuffer().addData(service);
	}

	@Override
	public void unbind(final Service service) {
		Dataset dataset;

		Collection<Airport> airports = this.repository.findAllAirports();

		SelectChoices airportChoices = new SelectChoices();
		airports.stream().forEach(a -> airportChoices.add(String.valueOf(a.getId()), String.format("%s - %s", a.getIATACode(), a.getCity()), false));

		dataset = super.unbindObject(service, "name", "picture", "avgDwellTime", "promotionCode", "money");

		dataset.put("defaultMoney", dataset.get("money") != null ? SupportedCurrency.convertToDefault(service.getMoney()) : "");

		dataset.put("airport", "0");
		dataset.put("airportChoices", airportChoices);

		super.getResponse().addData(dataset);
	}

}
