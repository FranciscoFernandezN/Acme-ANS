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
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.components.weather.WeatherPOJO;
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
	
	@Optional
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date forecastDate;
	
	@Optional
	@ValidNumber(min = -40., max = 60.)
	@Automapped
	private Double temperature;
	
	@Optional
	@ValidNumber(min = 0, max = 10000)
	@Automapped
	private Integer visibility;
	
	@Optional
	@ValidNumber(min = 0., max = 70.)
	@Automapped
	private Double rainPerHour;
	

	@Optional
	@ValidNumber(min = 0., max = 10.)
	@Automapped
	private Double snowPerHour;
	
	@Optional
	@ValidNumber(min = 0., max = 100.)
	@Automapped
	private Double wind;
	
	@Optional
	@ValidString
	@Automapped
	private String city;
	
	// Derived attributes -----------------------------------------------------
		
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
		
		final Integer BAD_VISIBILITY = 550;
		final Integer MID_VISIBILITY = 1000;
				
		WeatherStatus result;
		
		if(wind >= BAD_WIND || rainPerHour >= BAD_RAIN || snowPerHour >= BAD_SNOW || temperature <= BAD_TEMP_LOW || visibility <= BAD_VISIBILITY) {
			result = WeatherStatus.BAD_WEATHER;
		}else if(wind >= MID_WIND || snowPerHour >= MID_SNOW || rainPerHour >= MID_RAIN || temperature <= MID_TEMP_LOW || visibility <= MID_VISIBILITY) {
			result = WeatherStatus.MID_WEATHER;
		}else {
			result = WeatherStatus.GOOD_WEATHER;
		}
		
		return result;
	}
	
	// Relationships ----------------------------------------------------------

	// Ancillary methods ------------------------------------------------------
	
	public static Weather of(WeatherPOJO w, Date date, String city) {
		Weather weather = new Weather();
		weather.setCity(city);
		weather.setForecastDate(date);
		weather.setTemperature(w.getMain() != null ? w.getMain().getTemp() : 20);
		weather.setVisibility(w.getVisibility() != null ? w.getVisibility() : 10000);
		weather.setWind(w.getWind() != null ? w.getWind().getSpeed() : 0.);
		weather.setRainPerHour(w.getRain() != null ? w.getRain().getValue() : 0.);
		weather.setSnowPerHour(w.getSnow() != null ? w.getSnow().getValue() : 0.);
		
		return weather;
	}
}
