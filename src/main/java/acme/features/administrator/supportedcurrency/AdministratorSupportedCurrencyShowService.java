
package acme.features.administrator.supportedcurrency;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
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

		String defaultCurrency = PropertyHelper.getRequiredProperty("acme.currency.default", String.class);

		dataset = super.unbindObject(supportedCurrency, "currencyName");
		dataset.put("isDefaultCurrency", supportedCurrency.getCurrencyName() == null ? "N/A" : supportedCurrency.getCurrencyName().equals(defaultCurrency));

		super.getResponse().addData(dataset);
	}

}
