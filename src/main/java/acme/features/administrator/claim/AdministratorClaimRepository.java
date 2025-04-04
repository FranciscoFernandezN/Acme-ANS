
package acme.features.administrator.claim;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.trackinglogs.TrackingLog;

@Repository
public interface AdministratorClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where (c.indicator like 'ACCEPTED' or c.indicator like 'REJECTED') and c.isPublished = true")
	List<Claim> findAllPublishedClaims();

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select tl from TrackingLog tl where tl.claim.id = :id")
	TrackingLog findLogByClaimId(int id);
}
