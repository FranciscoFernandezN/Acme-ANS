
package acme.entities.aircrafts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import org.checkerframework.common.aliasing.qual.Unique;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				model;

	@Unique
	@Column(unique = true)
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				registrationNumber;

	@Mandatory
	@PositiveOrZero
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
	@ValidString(max = 255)
	@Automapped
	private String				details;

	// Derived attributes -----------------------------------------------------
	// Relationships ----------------------------------------------------------
	/*
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne
	 * private Airline airline;
	 */
}
