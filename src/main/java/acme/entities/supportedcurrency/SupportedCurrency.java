
package acme.entities.supportedcurrency;

import static org.mockito.ArgumentMatchers.doubleThat;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SupportedCurrency extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long						serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidCurrency
	@Column(unique = true)
	private String									currencyName;

	@Mandatory
	@Automapped
	private Boolean									isDefaultCurrency;

	// Derived attributes -----------------------------------------------------

	@Transient
	private static Map<String, Map<String, Double>>	lastData;


	@Transient
	public static String getDefaultCurrency() {
		SupportedCurrencyRepository repository = SpringHelper.getBean(SupportedCurrencyRepository.class);
		return repository.getDefaultCurrency();
	}

	@Transient
	public static Set<String> getAllowedCurrencies() {
		Set<String> res = new HashSet<>();

		if (SpringHelper.isRunningOn("production")) {
			try {
				String apiKey = "fca_live_LoNLzqN8xfOE524QdmeycQrAkUMvlwcsWGd5nEhw";
				String url = "https://api.freecurrencyapi.com/v1/currencies?apikey=" + apiKey;
				RestTemplate api = new RestTemplate();
				ResponseEntity<AllowedExchangePOJO> response = api.getForEntity(url, AllowedExchangePOJO.class);
				Map<String, Map<String, String>> data = response.getBody().getData();
				res = data.keySet();
			} catch (final Throwable oops) {
				System.out.println(oops);
			}
		} else
			res = Set.of("EUR", "USD", "JPY", "BGN", "CZK", "DKK", "GBP", "HUF", "PLN", "RON", "SEK", "CHF", "ISK", "RUB", "TRY", "AUD", "PHP", "ZAR");

		return res;
	}

	@Transient
	public static Money convertToDefault(final Money money) {
		Money result = new Money();
		String defaultCurrency = SupportedCurrency.getDefaultCurrency();
		result.setCurrency(defaultCurrency);
		if (!money.getCurrency().equals(defaultCurrency)) {
			if (SpringHelper.isRunningOn("production")) {
				try {
				
					String apiKey = "fca_live_LoNLzqN8xfOE524QdmeycQrAkUMvlwcsWGd5nEhw";
					Date now = MomentHelper.getCurrentMoment();
					@SuppressWarnings("deprecation")
					String nowFormatted = now.getYear() + 1900 + "-" + String.format("%02d", now.getMonth() + 1) + "-" + String.format("%02d", now.getDate());
					String url = "https://api.freecurrencyapi.com/v1/historical?apikey=" + apiKey + "&date=" + nowFormatted + "&base_currency=" + defaultCurrency;
					Map<String, Map<String, Double>> data;
					if (SupportedCurrency.lastData != null && SupportedCurrency.lastData.get(nowFormatted) != null)
						data = SupportedCurrency.lastData;
					else {
						RestTemplate api = new RestTemplate();
						ResponseEntity<ExchangePOJO> response = api.getForEntity(url, ExchangePOJO.class);
						data = response.getBody().getData();
						SupportedCurrency.lastData = data;
					}
	
					Double newAmount = money.getAmount() / data.values().iterator().next().get(money.getCurrency());
					result.setAmount(newAmount);
				} catch (final Throwable oops) {
					System.out.println(oops);
					result.setAmount(0.);
				}
			} else {
				Map<String, Double> mockedValues = new HashMap<>();
				if(money.getCurrency().equals("EUR")) {
					mockedValues.put("GBP", 0.8270029409);
					mockedValues.put("USD", 1.0352287186);
					Double newAmount = money.getAmount() / mockedValues.get(money.getCurrency());
					result.setAmount(newAmount);
				} else {
					result.setAmount(0.);
				}
				
			}
		} else
			result = money;
		
		return result;
	}

	// Relationships ----------------------------------------------------------
}
