package acme.features.authenticated.manager;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airlines.Airline;
import acme.realms.Manager;

public interface AuthenticatedManagerRepository extends AbstractRepository{
	
	@Query("SELECT m FROM Manager m WHERE m.userAccount.id = :id")
	Manager findManagerByUserAccountId(int id);
	
	@Query("SELECT a FROM Airline a WHERE a.id = :id")
	Airline findAirlineById(int id);
	
	@Query("SELECT a FROM Airline a")
	List<Airline> findAllAirlines();
	
	@Query("SELECT m FROM Manager m")
	List<Manager> findAllManagers();
	
	@Query("SELECT ua FROM UserAccount ua WHERE ua.id = :id")
	UserAccount findUserAccountById(int id);
	
}
