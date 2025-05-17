
package acme.features.any.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.services.Service;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiService
public class AnyServiceListService extends AbstractGuiService<Any, Service> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyServiceRepository sr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		List<Service> services;

		services = this.sr.findAllServices();

		super.getBuffer().addData(services);
	}

	@Override
	public void unbind(final Service service) {
		Dataset dataset;

		dataset = super.unbindObject(service, "name", "avgDwellTime", "promotionCode", "money");
		dataset.put("defaultMoney", dataset.get("money") != null ? SupportedCurrency.convertToDefault(service.getMoney()) : "");
		super.getResponse().addData(dataset);
	}

}
