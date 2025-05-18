
package acme.features.administrator.supportedcurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PropertyHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiService
public class AdministratorSupportedCurrencyShowService extends AbstractGuiService<Administrator, SupportedCurrency> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorSupportedCurrencyRepository scr;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		SupportedCurrency supportedCurrency;
		int supportedCurrencyId;

		supportedCurrencyId = super.getRequest().getData("id", int.class);
		supportedCurrency = this.scr.findSupportedCurrencyById(supportedCurrencyId);

		super.getBuffer().addData(supportedCurrency);
	}

	@Override
	public void unbind(final SupportedCurrency supportedCurrency) {
		Dataset dataset;

		dataset = super.unbindObject(supportedCurrency, "currencyName", "isDefaultCurrency");
		super.getResponse().addData(dataset);
	}

}
