
package acme.entities.bookings;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidSupportedCurrency;
import acme.entities.flights.Flight;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "isDraftMode"),
	@Index(columnList = "customer_id,isDraftMode"),
	@Index(columnList = "price_amount,purchaseMoment,customer_id,isDraftMode"),
	@Index(columnList = "id,isDraftMode")
})
public class Booking extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 6, max = 8, pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private TravelClass			travelClass;

	@Mandatory
	@ValidMoney(min = 0, max = 1000000)
	@ValidSupportedCurrency
	@Automapped
	private Money				price;

	@Optional
	@ValidString(min = 4, max = 4, pattern = "\\d{4}")
	@Automapped
	private String				lastNibble;

	@Mandatory
	@Automapped
	private Boolean				isDraftMode;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight				flight;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customer			customer;

}
