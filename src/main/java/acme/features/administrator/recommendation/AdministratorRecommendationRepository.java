
package acme.features.administrator.recommendation;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.recommendations.Recommendation;

@Repository
public interface AdministratorRecommendationRepository extends AbstractRepository {

	@Query("SELECT DISTINCT(a.city) FROM Airport a")
	List<String> findAllCities();

	@Query("SELECT DISTINCT(r.name) FROM Recommendation r")
	List<String> findAllRecommendationsNames();

	@Query("SELECT r FROM Recommendation r WHERE r.name = :name")
	Recommendation findRecommendationByName(String name);

}
