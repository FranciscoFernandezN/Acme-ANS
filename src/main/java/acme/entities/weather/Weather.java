
package acme.entities.weather;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.components.weather.WeatherPOJO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "city"),
	@Index(columnList = "forecastDate")
})
public class Weather extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Optional
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				forecastDate;

	@Optional
	@ValidNumber(min = -40., max = 60.)
	@Automapped
	private Double				temperature;

	@Optional
	@ValidNumber(min = 0, max = 10000)
	@Automapped
	private Integer				visibility;

	@Optional
	@ValidNumber(min = 0., max = 70.)
	@Automapped
	private Double				rainPerHour;

	@Optional
	@ValidNumber(min = 0., max = 10.)
	@Automapped
	private Double				snowPerHour;

	@Optional
	@ValidNumber(min = 0., max = 100.)
	@Automapped
	private Double				wind;

	@Optional
	@ValidString
	@Automapped
	private String				city;

	// Derived attributes -----------------------------------------------------


	@Transient
	public WeatherStatus getStatus() {
		final double BAD_WIND = 35.;
		final double MID_WIND = 20.;

		final double BAD_RAIN = 50.;
		final double MID_RAIN = 20.;

		final double BAD_SNOW = 2.5;
		final double MID_SNOW = 1.;

		final double BAD_TEMP_LOW = -5.;
		final double MID_TEMP_LOW = 0.;

		final int BAD_VISIBILITY = 550;
		final int MID_VISIBILITY = 1000;

		WeatherStatus result;

		if (this.wind >= BAD_WIND || this.rainPerHour >= BAD_RAIN || this.snowPerHour >= BAD_SNOW || this.temperature <= BAD_TEMP_LOW || this.visibility <= BAD_VISIBILITY)
			result = WeatherStatus.BAD_WEATHER;
		else if (this.wind >= MID_WIND || this.snowPerHour >= MID_SNOW || this.rainPerHour >= MID_RAIN || this.temperature <= MID_TEMP_LOW || this.visibility <= MID_VISIBILITY)
			result = WeatherStatus.MID_WEATHER;
		else
			result = WeatherStatus.GOOD_WEATHER;

		return result;
	}

	// Relationships ----------------------------------------------------------

	// Ancillary methods ------------------------------------------------------

	public static Weather of(final WeatherPOJO w, final Date date, final String city) {
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
