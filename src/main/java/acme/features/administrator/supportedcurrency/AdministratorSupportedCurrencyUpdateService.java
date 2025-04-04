
package acme.features.administrator.supportedcurrency;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.helpers.PropertyHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiService
public class AdministratorSupportedCurrencyUpdateService extends AbstractGuiService<Administrator, SupportedCurrency> {

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
	public void bind(final SupportedCurrency supportedCurrency) {
		super.bindObject(supportedCurrency, "currencyName");
	}

	@Override
	public void validate(final SupportedCurrency supportedCurrency) {

		String defaultCurrency = PropertyHelper.getRequiredProperty("acme.currency.default", String.class);

		List<SupportedCurrency> supportedCurrencies = this.scr.findAllSupportedCurrencies();
		List<String> currencyNames = supportedCurrencies.stream().filter(sp -> sp.getId() != supportedCurrency.getId()).map(sp -> sp.getCurrencyName()).toList();

		if (supportedCurrency.getCurrencyName() != null) {
			super.state(!supportedCurrency.getCurrencyName().equals(defaultCurrency), "currencyName", "administrator.supported-currency.create.is-default-currency");
			super.state(!currencyNames.contains(supportedCurrency.getCurrencyName()), "currencyName", "administrator.supported-currency.create.already-exists-currency");
		}
	}

	@Override
	public void perform(final SupportedCurrency supportedCurrency) {
		this.scr.save(supportedCurrency);
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
