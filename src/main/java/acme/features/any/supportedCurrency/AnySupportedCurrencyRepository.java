
package acme.features.any.supportedCurrency;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.supportedCurrency.SupportedCurrency;

@Repository
public interface AnySupportedCurrencyRepository extends AbstractRepository {

	@Query("SELECT sc FROM SupportedCurrency sc")
	List<SupportedCurrency> findAllSuportedCurrencies();
}
