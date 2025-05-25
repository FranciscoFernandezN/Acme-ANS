
package acme.entities.passengers;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
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
@Table(indexes = {
	@Index(columnList = "passportNumber"),
	@Index(columnList = "isDraftMode")
})
public class Passenger extends AbstractEntity {

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

	@Mandatory
	@ValidString(min = 6, max = 9, pattern = "^[A-Z0-9]{6,9}$")
	@Automapped
	private String				passportNumber;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				dateOfBirth;

	@Optional
	@ValidString(max = 50)
	@Automapped
	private String				specialNeeds;

	@Mandatory
	@Automapped
	private Boolean				isDraftMode;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
