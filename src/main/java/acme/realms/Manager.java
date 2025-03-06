
package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.checkerframework.common.aliasing.qual.Unique;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Manager extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	//TODO: revisar que el patrón esté bien a la hora de inicializar datos
	@Unique
	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2-3}\\d{6}$")
	@Mandatory
	@Column(unique = true)
	private String				identifierNumber;

	@Mandatory
	@ValidNumber(min = 0., max = 70., integer = 2, fraction = 0)
	@Automapped
	private Integer				yearsOfExperience;

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	private Date				birth;

	@Optional
	@Automapped
	private String				linkPicture;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
