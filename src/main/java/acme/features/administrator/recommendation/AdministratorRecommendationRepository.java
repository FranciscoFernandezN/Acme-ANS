
package acme.features.administrator.recommendation;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.recommendations.Recommendation;

@Repository
public interface AdministratorRecommendationRepository extends AbstractRepository {

	@Query("SELECT r FROM Recommendation r ORDER BY r.businessStatus DESC, r.openNow DESC, r.rating DESC, r.userRatingsTotal DESC")
	Collection<Recommendation> findAllRecommendation();

	@Query("SELECT DISTINCT(a.city) FROM Airport a")
	List<String> findAllCities();

	@Query("SELECT r.name FROM Recommendation r")
	List<String> findAllRecommendationsNames();

	@Query("SELECT r FROM Recommendation r WHERE r.name = :name")
	Recommendation findRecommendationByName(String name);

}
