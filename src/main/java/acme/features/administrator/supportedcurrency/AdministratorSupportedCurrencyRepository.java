package acme.features.administrator.supportedcurrency;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.supportedcurrency.SupportedCurrency;

@Repository
public interface AdministratorSupportedCurrencyRepository extends AbstractRepository{
	
	@Query("SELECT sc FROM SupportedCurrency sc")
	List<SupportedCurrency> findAllSupportedCurrencies();
	
	@Query("SELECT sc FROM SupportedCurrency sc WHERE sc.id = :id")
	SupportedCurrency findSupportedCurrencyById(int id);
	
}
