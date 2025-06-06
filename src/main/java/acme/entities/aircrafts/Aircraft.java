
package acme.entities.aircrafts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidNonSpamString;
import acme.entities.airlines.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "airline_id,isEnabled")
})
public class Aircraft extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidNonSpamString
	@ValidString(max = 50)
	@Automapped
	private String				model;

	@Column(unique = true)
	@Mandatory
	@ValidNonSpamString
	@ValidString(max = 50)
	private String				registrationNumber;

	@Mandatory
	@ValidNumber(min = 0, max = 750)
	@Automapped
	private Integer				capacity;

	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	@Automapped
	private Integer				cargoWeight;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private AircraftStatus		status;

	@Optional
	@ValidNonSpamString
	@ValidString(max = 255)
	@Automapped
	private String				details;

	@Mandatory
	@Automapped
	private Boolean				isEnabled;

	// Derived attributes -----------------------------------------------------
	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;

}
