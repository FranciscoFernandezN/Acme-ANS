
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.supportedCurrency.SupportedCurrency;
import acme.features.any.supportedCurrency.AnySupportedCurrencyRepository;

@Validator
public class SupportedCurrencyValidator extends AbstractValidator<ValidSupportedCurrency, Money> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnySupportedCurrencyRepository scr;

	// Initialiser ------------------------------------------------------------


	@Override
	protected void initialise(final ValidSupportedCurrency annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Money money, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		List<SupportedCurrency> supportedCurrencies = this.scr.findAllSuportedCurrencies();
		if (money != null && supportedCurrencies.stream().map(SupportedCurrency::getCurrencyName).noneMatch(cn -> cn.equals(money.getCurrency())))
			super.state(context, false, "currency", "acme.validation.currency.message");
		result = !super.hasErrors(context);
		return result;
	}

}
