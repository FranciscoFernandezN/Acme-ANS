
package acme.entities.flights;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidSupportedCurrency;
import acme.entities.airlines.Airline;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.entities.weather.Weather;
import acme.entities.weather.WeatherStatus;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(indexes = {
	@Index(columnList = "is_draft_mode")
})
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

	@Mandatory
	@Automapped
	private Boolean				isDraftMode;

	// Derived attributes -----------------------------------------------------


	//TODO: no me gusta esto de aquí
	private List<Leg> getSortedLegs() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		List<Leg> legs = repository.findAllLegsByFlightId(this.getId());
		Comparator<Leg> c = Comparator.comparing(Leg::getScheduledDeparture);
		return legs.stream().sorted(c).toList();
	}

	private Leg getFirstLeg() {
		List<Leg> sortedLegs = this.getSortedLegs();
		return sortedLegs.isEmpty() ? null : sortedLegs.getFirst();
	}

	private Leg getLastLeg() {
		List<Leg> sortedLegs = this.getSortedLegs();
		return sortedLegs.isEmpty() ? null : sortedLegs.getLast();
	}

	@Transient
	public Date getScheduledDeparture() {
		Leg firstLeg = this.getFirstLeg();
		return firstLeg == null ? null : firstLeg.getScheduledDeparture();
	}

	@Transient
	public Date getScheduledArrival() {
		Leg lastLeg = this.getLastLeg();
		return lastLeg == null ? null : lastLeg.getScheduledArrival();
	}

	@Transient
	public Airport getOriginAirport() {
		Leg firstLeg = this.getFirstLeg();
		return firstLeg == null ? null : firstLeg.getDepartureAirport();
	}

	@Transient
	public String getOrigin() {
		Leg firstLeg = this.getFirstLeg();
		return firstLeg == null ? null : firstLeg.getDepartureAirport().getCity();
	}

	@Transient
	public Airport getDestinyAirport() {
		Leg lastLeg = this.getLastLeg();
		return lastLeg == null ? null : lastLeg.getArrivalAirport();
	}

	@Transient
	public String getDestiny() {
		Leg lastLeg = this.getLastLeg();
		return lastLeg == null ? null : lastLeg.getArrivalAirport().getCity();
	}

	@Transient
	public Integer getNumberOfLayovers() {
		Integer size = this.getSortedLegs().size();
		return size <= 0 ? 0 : size - 1;
	}

	@Transient
	public Airline getAirline() {
		return this.getManager().getAirlineManaging();
	}

	@Transient
	public Boolean getFlownWithBadWeather() {
		Boolean result = null;
		List<Leg> legs = this.getSortedLegs();
		if (!legs.isEmpty()) {
			result = false;
			FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
			for (Leg l : legs) {
				List<Weather> listOriginWeather = repository.findWeatherByCity(l.getDepartureAirport().getCity());
				List<Weather> listDestinyWeather = repository.findWeatherByCity(l.getArrivalAirport().getCity());
				Comparator<Weather> timeComparatorOrigin = Comparator.comparing((final Weather w) -> (w.getForecastDate().getTime() - l.getScheduledDeparture().getTime()));
				Comparator<Weather> timeComparatorDestiny = Comparator.comparing((final Weather w) -> (w.getForecastDate().getTime() - l.getScheduledArrival().getTime()));
				Weather originWeather = listOriginWeather.stream().min(timeComparatorOrigin).orElse(null);
				Weather destinyWeather = listDestinyWeather.stream().min(timeComparatorDestiny).orElse(null);
				if (originWeather != null && originWeather.getStatus().equals(WeatherStatus.BAD_WEATHER) || destinyWeather != null && destinyWeather.getStatus().equals(WeatherStatus.BAD_WEATHER)) {
					result = true;
					break;
				}

			}
		}
		return result;
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager manager;
}
