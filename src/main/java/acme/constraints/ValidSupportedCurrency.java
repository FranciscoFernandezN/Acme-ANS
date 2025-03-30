
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupportedCurrencyValidator.class)

public @interface ValidSupportedCurrency {

	// Standard validation properties -----------------------------------------

	String message() default "";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
