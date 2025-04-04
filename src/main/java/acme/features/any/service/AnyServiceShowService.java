
package acme.features.any.service;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.services.Service;

@GuiService
public class AnyServiceShowService extends AbstractGuiService<Any, Service> {

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
		Service service;
		int serviceId;

		serviceId = super.getRequest().getData("id", int.class);
		service = this.sr.findServiceById(serviceId);

		super.getBuffer().addData(service);
	}

	@Override
	public void unbind(final Service service) {
		Dataset dataset;

		dataset = super.unbindObject(service, "name", "picture", "avgDwellTime", "promotionCode", "money");
		dataset.put("airport", service.getAirport().getIATACode());

		super.getResponse().addData(dataset);
	}

}
