
package acme.features.assistanceAgent.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;

public interface AssistanceAgentDashboardRepository extends AbstractRepository {

	@Query("SELECT 1.0 * COUNT(c1) / (SELECT COUNT(c2) FROM Claim c2 WHERE c2.agent.id = :id) FROM Claim c1 WHERE c1.indicator LIKE 'ACCEPTED' AND c1.agent.id = :id")
	Double ratioOfClaimsResolved(int id);

	@Query("SELECT 1.0 * COUNT(c1) / (SELECT COUNT(c2) FROM Claim c2 WHERE c2.agent.id = :id) FROM Claim c1 WHERE c1.indicator LIKE 'REJECTED' AND c1.agent.id = :id")
	Double ratioOfClaimsRejected(int id);

	@Query("SELECT c.registrationMoment FROM Claim c WHERE c.agent.id = :id")
	List<Date> findAllRegistrationMomentsByAgentId(int id);

	@Query("SELECT c FROM Claim c WHERE c.agent.id = :id")
	List<Claim> findAllClaimsByAgentId(int id);

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :id")
	List<TrackingLog> logsOfClaim(int id);
}
