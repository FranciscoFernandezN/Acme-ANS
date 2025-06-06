package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AntiSpamValidator.class)

public @interface ValidNonSpamString {
	
	// Standard validation properties -----------------------------------------

	String message() default "{placeholder}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
}
