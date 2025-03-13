
package acme.realms;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

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
public class Ban extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

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
	@ValidMoment(past = false)
	@Temporal(TemporalType.DATE)
	private Date				liftDate;

	// Derived attributes -----------------------------------------------------


	@Transient
	public Boolean isStillBanned() {
		return this.liftDate == null || this.liftDate.after(new Date());
	}

	// Relationships ----------------------------------------------------------

}
