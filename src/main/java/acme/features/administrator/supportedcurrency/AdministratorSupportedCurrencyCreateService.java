
package acme.features.administrator.supportedcurrency;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.supportedcurrency.SupportedCurrency;

@GuiService
public class AdministratorSupportedCurrencyCreateService extends AbstractGuiService<Administrator, SupportedCurrency> {

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

		supportedCurrency = new SupportedCurrency();

		super.getBuffer().addData(supportedCurrency);
	}

	@Override
	public void bind(final SupportedCurrency supportedCurrency) {
		super.bindObject(supportedCurrency, "currencyName", "isDefaultCurrency");
	}

	@Override
	public void validate(final SupportedCurrency supportedCurrency) {

		List<SupportedCurrency> supportedCurrencies = this.scr.findAllSupportedCurrencies();
		List<String> currencyNames = supportedCurrencies.stream().map(sp -> sp.getCurrencyName()).toList();

		super.state(!currencyNames.contains(supportedCurrency.getCurrencyName()), "currencyName", "administrator.supported-currency.create.already-exists-currency");

	}

	@Override
	public void perform(final SupportedCurrency supportedCurrency) {
		if (supportedCurrency.getIsDefaultCurrency()) {
			SupportedCurrency defaultCurrency = this.scr.findDefaultSupportedCurrency();
			defaultCurrency.setIsDefaultCurrency(false);
			this.scr.save(defaultCurrency);
		}
		this.scr.save(supportedCurrency);
	}

	@Override
	public void unbind(final SupportedCurrency supportedCurrency) {
		Dataset dataset;

		dataset = super.unbindObject(supportedCurrency, "currencyName", "isDefaultCurrency");
		super.getResponse().addData(dataset);
	}

}
