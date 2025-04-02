package acme.entities.weather;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.entities.airports.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Weather extends AbstractEntity {
	
	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------
	
	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date forecastDate;
	
	@Mandatory
	@ValidNumber(min = -40., max = 60.)
	@Automapped
	private Double temperature;
	
	@Mandatory
	@ValidNumber(min = 0., max = 10.)
	@Automapped
	private Double visibility;
	
	@Mandatory
	@ValidNumber(min = 0., max = 70.)
	@Automapped
	private Double rainPerHour;
	

	@Mandatory
	@ValidNumber(min = 0., max = 10.)
	@Automapped
	private Double snowPerHour;
	
	@Mandatory
	@ValidNumber(min = 0., max = 100.)
	@Automapped
	private Double wind;
	
	// Derived attributes -----------------------------------------------------
	
	@Transient
	public String getCity() {
		return airport.getCity();
	}
	
	@Transient
	public WeatherStatus getStatus() {
		final Double BAD_WIND = 35.;
		final Double MID_WIND = 20.;
		
		final Double BAD_RAIN = 50.;
		final Double MID_RAIN = 20.;
		
		final Double BAD_SNOW = 2.5;
		final Double MID_SNOW = 1.;
		
		final Double BAD_TEMP_LOW = -5.;
		final Double MID_TEMP_LOW = 0.;
				
		WeatherStatus result;
		
		if(wind >= BAD_WIND || rainPerHour >= BAD_RAIN || snowPerHour >= BAD_SNOW || temperature <= BAD_TEMP_LOW) {
			result = WeatherStatus.valueOf("BAD_WEATHER");
		}else if((wind <= BAD_WIND && wind >= MID_WIND) || (snowPerHour <= BAD_SNOW && snowPerHour >= MID_SNOW) ||
			(rainPerHour <= BAD_RAIN && rainPerHour >= MID_RAIN) || (temperature >= BAD_TEMP_LOW && temperature <= MID_TEMP_LOW)) {
			result = WeatherStatus.valueOf("MID_WEATHER");
		}else {
			result = WeatherStatus.valueOf("GOOD_WEATHER");
		}
		
		return result;
	}
	
	// Relationships ----------------------------------------------------------
	
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport airport;
}
