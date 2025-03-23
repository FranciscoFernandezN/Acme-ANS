
package acme.features.any.review;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.reviews.Review;

@Repository
public interface AnyReviewRepository extends AbstractRepository {

	@Query("Select r FROM Review r WHERE r.postedAt > :minimumDate")
	List<Review> findReviewsNewerThanDate(Date minimumDate);

}
