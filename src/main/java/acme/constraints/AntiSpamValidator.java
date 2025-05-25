package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.features.any.supportedCurrency.AnySupportedCurrencyRepository;
import acme.internals.helpers.HibernateHelper;
import detection.SpamDetector;
import util.SpamProperties;

@Validator
public class AntiSpamValidator extends AbstractValidator<ValidNonSpamString, String>{
	
	// Internal state ---------------------------------------------------------

	@Autowired
	private AnySupportedCurrencyRepository scr;

	// Initialiser ------------------------------------------------------------


	@Override
	protected void initialise(final ValidNonSpamString annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final String text, final ConstraintValidatorContext context) {
		assert context != null;
		boolean condition;
		if(StringHelper.isBlank(text)) {
			condition = true;
		} else {
			condition = !SpamDetector.isSpam(text, new SpamProperties());
			if(!condition)
				HibernateHelper.replaceParameter(context, "placeholder", "acmeans.validation.nonspam.message");
		}
		
		return condition;
	}
	
}
