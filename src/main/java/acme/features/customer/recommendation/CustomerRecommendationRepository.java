
package acme.features.customer.recommendation;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.recommendations.Recommendation;

@Repository
public interface CustomerRecommendationRepository extends AbstractRepository {

	@Query("SELECT r FROM Recommendation r ORDER BY r.businessStatus DESC, r.openNow DESC, r.rating DESC, r.userRatingsTotal DESC")
	List<Recommendation> findAllRecommendation();

	@Query("SELECT r FROM Recommendation r WHERE r.id = :id")
	Recommendation findRecommendationById(int id);

	@Query("SELECT r FROM Recommendation r WHERE r.city = :city")
	List<Recommendation> findRecommendationsByCity(String city);

}
