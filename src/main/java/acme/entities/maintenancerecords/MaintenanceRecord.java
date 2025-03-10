
package acme.entities.maintenancerecords;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Min;

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
public class MaintenanceRecord extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				moment;

	@Mandatory
	@Valid
	@Enumerated(EnumType.STRING)
	@Automapped
	private MaintenanceStatus	status;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				nextInspectionDue;

	@Mandatory
	@Min(0)
	@Automapped
	private double				estimatedCost;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				notes;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}
