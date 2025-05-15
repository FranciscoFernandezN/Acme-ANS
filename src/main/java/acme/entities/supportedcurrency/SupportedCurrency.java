
package acme.entities.supportedcurrency;

import java.beans.Transient;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidCurrency;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.components.exchange.ExchangePOJO;
import acme.components.weather.WeatherPOJO;
import acme.entities.flights.FlightRepository;
import acme.entities.weather.Weather;
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
	
	@Mandatory
	@Automapped
	private Boolean isDefaultCurrency;

	// Derived attributes -----------------------------------------------------
	
	@Transient
	public static String getDefaultCurrency() {
		SupportedCurrencyRepository repository = SpringHelper.getBean(SupportedCurrencyRepository.class);
		return repository.getDefaultCurrency();
	}
	
	@Transient
	public static Money convertToDefault(Money money) {
		try {
			String defaultCurrency = SupportedCurrency.getDefaultCurrency();
			String apiKey = "eaec41a4fab107fa1e81e199";
			String url = "https://v6.exchangerate-api.com/v6/ " + apiKey + "/latest/" + defaultCurrency;
			RestTemplate api = new RestTemplate();
			
			ResponseEntity<ExchangePOJO> response = api.getForEntity(url, ExchangePOJO.class);
			Double newAmount = money.getAmount() / response.getBody().getConversionRates().get(money.getCurrency());
			Money result = new Money();
			result.setAmount(newAmount);
			result.setCurrency(defaultCurrency);
			return result;
		} catch (final Throwable oops) {
			System.out.println(oops);
			Money result = new Money();
			result.setAmount(0.);
			result.setCurrency("EUR");
			return result;
		}
	}
	
	// Relationships ----------------------------------------------------------
}
