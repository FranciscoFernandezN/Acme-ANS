
package acme.features.assistanceAgent.trackingLog;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("select tl from TrackingLog tl where tl.agent.id = :id")
	List<TrackingLog> findAllTrackingLogsByAgentId(int id);

	@Query("select c from Claim c where c.agent.airline.id = :id")
	List<Claim> findAllClaimsByAirlineId(int id);

	@Query("select c from Claim c where c.agent.id = :id")
	List<Claim> findAllClaimsByAgentId(int id);

	@Query("select c from Claim c, TrackingLog tl where tl.id = :id and c.id = tl.claim.id")
	Claim findClaimByTrackingLogId(int id);

	@Query("select tl from TrackingLog tl where tl.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);
}
