
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

@GuiService
public class AdministratorServiceUpdateService extends AbstractGuiService<Administrator, Service> {

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
	public void bind(final Service service) {
		int airportId;
		Airport airport;

		super.bindObject(service, "name", "picture", "avgDwellTime", "promotionCode", "money");

		airportId = super.getRequest().getData("airport", int.class);
		airport = this.repository.findAirportById(airportId);

		service.setAirport(airport);

	}

	@Override
	public void validate(final Service service) {
		int airportId;
		Airport airport;
		Service oldService;
		Boolean promoAlreadyUsed;

		airportId = super.getRequest().getData("airport", int.class);
		airport = this.repository.findAirportById(airportId);
		oldService = this.repository.findServiceById(service.getId());
		promoAlreadyUsed = !this.repository.findPromotionCodes().contains(service.getPromotionCode()) || oldService.getPromotionCode().equals(service.getPromotionCode());

		super.state(service.getPromotionCode().isBlank() || promoAlreadyUsed, "promotionCode", "administrator.service.create.promotion-code-must-be-unique");

		super.state(airport != null, "airport", "administrator.service.create.airport-does-not-exist");

	}

	@Override
	public void perform(final Service service) {
		this.repository.save(service);
	}

	@Override
	public void unbind(final Service service) {
		Dataset dataset;

		Collection<Airport> airports = this.repository.findAllAirports();

		SelectChoices airportChoices = new SelectChoices();
		airports.stream().forEach(a -> airportChoices.add(String.valueOf(a.getId()), String.format("%s - %s", a.getIATACode(), a.getCity()), false));

		dataset = super.unbindObject(service, "name", "picture", "avgDwellTime", "promotionCode", "money");
		dataset.put("airport", "0");
		dataset.put("airportChoices", airportChoices);

		super.getResponse().addData(dataset);
	}

}
