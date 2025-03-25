
package acme.entities.flights;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidSupportedCurrency;
import acme.entities.airlines.Airline;
import acme.entities.legs.Leg;
import acme.entities.reviews.Review;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Automapped
	private Boolean				needsSelfTransfer;

	@Mandatory
	@ValidMoney(min = 0)
	@ValidSupportedCurrency
	@Automapped
	private Money				cost;

	@Optional
	@ValidString
	@Automapped
	private String				description;

	// Derived attributes -----------------------------------------------------


	private Leg getFirstLeg() {
		Comparator<Leg> c = Comparator.comparing((final Leg l) -> l.getScheduledDeparture());
		List<Leg> listaOrdenada = this.legs.stream().sorted(c).toList();
		return listaOrdenada.getFirst();
	}

	private Leg getLastLeg() {
		Comparator<Leg> c = Comparator.comparing((final Leg l) -> l.getScheduledDeparture());
		List<Leg> listaOrdenada = this.legs.stream().sorted(c).toList();
		return listaOrdenada.getLast();
	}

	@Transient
	private Date getScheduledDeparture() {
		return this.getFirstLeg().getScheduledDeparture();
	}

	@Transient
	private Date getScheduledArrival() {
		return this.getLastLeg().getScheduledArrival();
	}

	@Transient
	private String getOrigin() {
		return this.getFirstLeg().getDepartureAirport().getCity();
	}

	@Transient
	private String getDestiny() {
		return this.getLastLeg().getArrivalAirport().getCity();
	}

	@Transient
	private Integer getNumberOfLayovers() {
		Integer size = this.legs.size();
		return size <= 0 ? 0 : size - 1;
	}

	// Relationships ----------------------------------------------------------


	@Optional
	@Valid
	@OneToMany
	private List<Leg>		legs;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline			airline;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager			managerOfFlight;

	@Optional
	@Valid
	@OneToMany
	private List<Review>	reviews;

}
