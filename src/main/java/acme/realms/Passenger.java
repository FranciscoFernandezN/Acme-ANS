
package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.checkerframework.common.aliasing.qual.Unique;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Passenger extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString
	@Automapped
	private String				fullName;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				email;

	@Unique
	@Mandatory
	@ValidString(min = 6, max = 9, pattern = "^[A-Z0-9]{6,9}$")
	@Column(unique = true)
	private String				passportNumber;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				dateOfBirth;

	@Optional
	@ValidString(max = 50)
	@Automapped
	private String				specialNeeds;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
