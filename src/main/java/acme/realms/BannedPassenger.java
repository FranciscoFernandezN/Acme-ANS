
package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.checkerframework.common.aliasing.qual.Unique;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BannedPassenger extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString
	@Automapped
	private String				fullName;

	@Unique
	@Mandatory
	@ValidString(min = 6, max = 9, pattern = "^[A-Z0-9]{6,9}$")
	@Column(unique = true)
	private String				passportNumber;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				dateOfBirth;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				nationality;

	@Mandatory
	@ValidString
	@Automapped
	private String				reasonForBan;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				banIssuedDate;

	@Optional
	@ValidMoment(past = true)
	private Date				liftDate;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
