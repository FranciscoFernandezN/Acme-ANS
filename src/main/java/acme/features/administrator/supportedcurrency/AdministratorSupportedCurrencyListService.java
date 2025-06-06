
package acme.features.administrator.supportedcurrency;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiService
public class AdministratorSupportedCurrencyListService extends AbstractGuiService<Administrator, SupportedCurrency> {

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
		List<SupportedCurrency> supportedCurrencies;

		supportedCurrencies = this.scr.findAllSupportedCurrencies();

		super.getBuffer().addData(supportedCurrencies);
	}

	@Override
	public void unbind(final SupportedCurrency supportedCurrency) {
		Dataset dataset;

		dataset = super.unbindObject(supportedCurrency, "currencyName");

		dataset.put("isDefaultCurrency", supportedCurrency.getIsDefaultCurrency() ? "✓" : "x");

		super.getResponse().addData(dataset);
	}

}
