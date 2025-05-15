package acme.entities.supportedcurrency;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface SupportedCurrencyRepository extends AbstractRepository {
	
	@Query("SELECT c.currencyName FROM SupportedCurrency c WHERE c.isDefaultCurrency = true")
	String getDefaultCurrency();
	
}
