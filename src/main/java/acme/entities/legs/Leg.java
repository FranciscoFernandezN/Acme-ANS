
package acme.entities.legs;

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
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airlines.Airline;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "flight_id"),
	@Index(columnList = "flight_id,scheduled_departure"),
	@Index(columnList = "aircraft_airline_id,is_draft_mode"),
	@Index(columnList = "status,manager_id"),
	@Index(columnList = "manager_id")
})
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(min = 4, max = 4, pattern = "\\d{4}")
	@Mandatory
	@Column(unique = true)
	private String				uniqueIdentifier;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private LegStatus			status;

	@Mandatory
	@Automapped
	private Boolean				isDraftMode;

	// Derived attributes -----------------------------------------------------


	@Transient
	public String getFlightNumber() {
		return this.getAirline().getIATACode() + this.uniqueIdentifier;
	}

	@Transient
	public Double getDuration() {
		double milis = Math.abs(this.scheduledDeparture.getTime() - this.scheduledArrival.getTime());
		return milis / (1000 * 60 * 60);
	}

	@Transient
	public Airline getAirline() {
		return this.aircraft.getAirline();
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		departureAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft	aircraft;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager		manager;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight		flight;
}
