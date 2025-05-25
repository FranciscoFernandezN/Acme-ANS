
package acme.features.assistanceAgent.claim;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.legs.Leg;
import acme.entities.trackinglogs.TrackingLog;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where (c.indicator like 'ACCEPTED' or c.indicator like 'REJECTED') and c.agent.id = :id")
	List<Claim> findAllCompletedClaimsByAgentId(int id);

	@Query("select c from Claim c where (c.indicator like 'IN_PROGRESS') and c.agent.id = :id")
	List<Claim> findAllInProgressClaimsByAgentId(int id);

	@Query("select l from Leg l where l.aircraft.airline.id = :id and l.isDraftMode = false")
	List<Leg> findAllLegsByAirlineId(int id);

	@Query("select tl from TrackingLog tl where tl.claim.id = :id")
	List<TrackingLog> findTrackingLogsByClaimId(int id);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select l from Leg l, Claim c where c.id = :id and l.id = c.leg.id")
	Leg findLegByClaimId(int id);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);
}
