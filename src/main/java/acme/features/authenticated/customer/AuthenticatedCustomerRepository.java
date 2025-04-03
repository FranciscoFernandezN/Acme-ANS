
package acme.features.authenticated.customer;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.realms.Customer;

public interface AuthenticatedCustomerRepository extends AbstractRepository {

	@Query("SELECT c FROM Customer c WHERE c.userAccount.id = :id")
	Customer findCustomerByUserAccountId(int id);

	@Query("SELECT c FROM Customer c")
	Collection<Customer> findAllCustomers();

	@Query("SELECT ua FROM UserAccount ua WHERE ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("SELECT c FROM Customer c WHERE c.id = :id")
	Customer findCustomerById(int id);

}
