
package acme.entities.tasks;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.maintenancerecords.MaintenanceRecord;
import acme.realms.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Task extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Valid
	@Enumerated(EnumType.STRING)
	@Automapped
	private TaskType			type;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 100)
	@Automapped
	private Integer				priority;

	//prueba 2
	@Mandatory
	@Min(0)
	@Automapped
	private Double				estimatedDuration;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne
	@Automapped
	private Technician			assignedTechnician;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private MaintenanceRecord	relatedMaintenance;

}
