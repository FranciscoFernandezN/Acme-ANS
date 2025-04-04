
package acme.entities.supportedcurrency;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidCurrency;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SupportedCurrency extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidCurrency
	@Column(unique = true)
	private String				currencyName;

	// Derived attributes -----------------------------------------------------
	// Relationships ----------------------------------------------------------
}
