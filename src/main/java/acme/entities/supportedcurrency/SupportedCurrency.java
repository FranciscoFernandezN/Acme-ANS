
package acme.entities.supportedcurrency;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidCurrency;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.components.exchange.AllowedExchangePOJO;
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
	private static Map<String, Map<String, Double>> lastData;
	
	@Transient
	public static String getDefaultCurrency() {
		SupportedCurrencyRepository repository = SpringHelper.getBean(SupportedCurrencyRepository.class);
		return repository.getDefaultCurrency();
	}
	
	@Transient
	public static Set<String> getAllowedCurrencies() {
		try {
			String apiKey = "fca_live_LoNLzqN8xfOE524QdmeycQrAkUMvlwcsWGd5nEhw";
			String url = "https://api.freecurrencyapi.com/v1/currencies?apikey=" + apiKey;
			RestTemplate api = new RestTemplate();
			ResponseEntity<AllowedExchangePOJO> response = api.getForEntity(url, AllowedExchangePOJO.class);
			Map<String, Map<String, String>> data = response.getBody().getData();
			return data.keySet();
		} catch (final Throwable oops) {
			System.out.println(oops);
			return new HashSet<>();
		}
	}
	
	@Transient
	public static Money convertToDefault(Money money) {
		try {
			String defaultCurrency = SupportedCurrency.getDefaultCurrency();
			if(!money.getCurrency().equals(defaultCurrency)) {
				String apiKey = "fca_live_LoNLzqN8xfOE524QdmeycQrAkUMvlwcsWGd5nEhw";
				Date now = MomentHelper.getCurrentMoment();
				String nowFormatted = (now.getYear() + 1900) + "-" + String.format("%02d", (now.getMonth() + 1)) + "-" + String.format("%02d", now.getDate());
				String url = "https://api.freecurrencyapi.com/v1/historical?apikey=" + apiKey + "&date=" + nowFormatted + "&base_currency=" + defaultCurrency;
				Map<String, Map<String, Double>> data;
				if(lastData != null && lastData.get(nowFormatted) != null) {
					 data = lastData;
				} else {
					RestTemplate api = new RestTemplate();
					ResponseEntity<ExchangePOJO> response = api.getForEntity(url, ExchangePOJO.class);
					data = response.getBody().getData();
					lastData = data;
				}
				
				Double newAmount = money.getAmount() / data.values().iterator().next().get(money.getCurrency());
				Money result = new Money();
				result.setAmount(newAmount);
				result.setCurrency(defaultCurrency);
				return result;
			} else {
				return money;
			}
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
