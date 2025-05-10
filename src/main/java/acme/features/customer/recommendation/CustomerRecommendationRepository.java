
package acme.features.customer.recommendation;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.recommendations.Recommendation;

@Repository
public interface CustomerRecommendationRepository extends AbstractRepository {

	@Query("SELECT r FROM Recommendation r ORDER BY r.businessStatus DESC, r.openNow DESC, r.rating DESC, r.userRatingsTotal DESC")
	Collection<Recommendation> findAllRecommendation();

	@Query("SELECT r FROM Recommendation r WHERE r.id = :id")
	Recommendation findRecommendationById(int id);

	@Query("SELECT r FROM Recommendation r WHERE r.city = :city ORDER BY r.businessStatus DESC, r.openNow DESC, r.rating DESC, r.userRatingsTotal DESC")
	Collection<Recommendation> findRecommendationsByCity(String city);

	@Query("SELECT r FROM Recommendation r WHERE r.city IN :cities ORDER BY r.businessStatus DESC, r.openNow DESC, r.rating DESC, r.userRatingsTotal DESC")
	Collection<Recommendation> findRecommendationsByCities(List<String> cities);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(int customerId);

}
