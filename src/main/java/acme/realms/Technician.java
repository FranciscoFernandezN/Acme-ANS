
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;

import org.checkerframework.common.aliasing.qual.Unique;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Technician extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Unique
	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String				licenseNumber;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				specialisation;

	@Mandatory
	private boolean				annualHealthTestPassed;

	@Mandatory
	@Min(0)
	private int					yearsOfExperience;

	@Optional
	@ValidString(max = 255)
	private String				certifications;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}
