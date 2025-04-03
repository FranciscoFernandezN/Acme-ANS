
package acme.features.authenticated.assistanceAgent;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airlines.Airline;
import acme.realms.AssistanceAgent;

public interface AuthenticatedAssistanceAgentRepository extends AbstractRepository {

	@Query("SELECT aa FROM AssistanceAgent aa WHERE aa.userAccount.id = :id")
	AssistanceAgent findAssistanceAgentByUserAccountId(int id);

	@Query("SELECT a FROM Airline a WHERE a.id = :id")
	Airline findAirlineById(int id);

	@Query("SELECT a FROM Airline a")
	List<Airline> findAllAirlines();

	@Query("SELECT aa FROM AssistanceAgent aa")
	List<AssistanceAgent> findAllAssistanceAgents();

	@Query("SELECT ua FROM UserAccount ua WHERE ua.id = :id")
	UserAccount findUserAccountById(int id);

}
