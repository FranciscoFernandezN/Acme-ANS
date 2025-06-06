
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidNonSpamString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidNonSpamString
	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String				identifier;

	@Mandatory
	@ValidNonSpamString
	@ValidString(min = 6, max = 15, pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidNonSpamString
	@ValidString
	@Automapped
	private String				physicalAddress;

	@Mandatory
	@ValidNonSpamString
	@ValidString(max = 50)
	@Automapped
	private String				city;

	@Mandatory
	@ValidNonSpamString
	@ValidString(max = 50)
	@Automapped
	private String				country;

	@Optional
	@ValidNumber(min = 0, max = 500000)
	@Automapped
	private Integer				earnedPoints;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
