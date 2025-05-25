
package acme.features.administrator.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AdministratorDashboardRepository extends AbstractRepository {

	// Conteo de aeropuertos por operationalScope
	@Query("SELECT a.operationalScope, COUNT(a) FROM Airport a GROUP BY a.operationalScope")
	List<Object[]> countAirportsByOperationalScope();

	// Conteo de aerolíneas por tipo
	@Query("SELECT a.airlineType, COUNT(a) FROM Airline a GROUP BY a.airlineType")
	List<Object[]> countAirlinesByType();

	// Conteo de aerolíneas con email y teléfono no nulos
	@Query("SELECT COUNT(a) FROM Airline a WHERE a.email IS NOT NULL AND a.contactNumber IS NOT NULL")
	Integer countAirlinesWithEmailAndPhone();

	// Conteo total de aerolíneas
	@Query("SELECT COUNT(a) FROM Airline a")
	Integer countAllAirlines();

	// Conteo de aeronaves habilitadas
	@Query("SELECT COUNT(ac) FROM Aircraft ac WHERE ac.isEnabled = true")
	Integer countEnabledAircrafts();

	// Conteo total de aeronaves
	@Query("SELECT COUNT(ac) FROM Aircraft ac")
	Integer countAllAircrafts();

	// Conteo de reseñas con score > 5
	@Query("SELECT COUNT(r) FROM Review r WHERE r.score > 5")
	Integer countReviewsWithScoreAboveFive();

	// Conteo total de reseñas
	@Query("SELECT COUNT(r) FROM Review r")
	Integer countAllReviews();

	// Conteo de reseñas desde fecha
	@Query("SELECT COUNT(r) FROM Review r WHERE r.postedAt >= :tenWeeksAgo")
	Integer countReviewsPostedSince(Date tenWeeksAgo);

	// Conteo de reseñas agrupadas por semana desde fecha
	@Query("SELECT FUNCTION('WEEK', r.postedAt), COUNT(r) FROM Review r WHERE r.postedAt >= :tenWeeksAgo GROUP BY FUNCTION('WEEK', r.postedAt)")
	List<Object[]> countReviewsPerWeekSince(Date tenWeeksAgo);
}
