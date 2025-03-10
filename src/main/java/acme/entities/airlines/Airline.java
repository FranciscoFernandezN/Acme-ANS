
package acme.entities.airlines;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;

import org.checkerframework.common.aliasing.qual.Unique;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Airline extends AbstractEntity {

	// Serialisation version --------------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				name;

	@Unique
	@Mandatory
	@ValidString(min = 3, max = 3, pattern = "[A-Z]{2}X")
	@Column(unique = true)
	private String				iATACode;

	@Optional
	@ValidUrl
	@Automapped
	private String				website;

	@Mandatory
	@Valid
	@Enumerated(EnumType.STRING)
	@Automapped
	private AirlineType			type;

	@Mandatory
	@Valid
	@Automapped
	private LocalDate			foundationMoment;

	@Optional
	@ValidEmail
	@Automapped
	private String				email;

	@Optional
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				contactNumber;
}
