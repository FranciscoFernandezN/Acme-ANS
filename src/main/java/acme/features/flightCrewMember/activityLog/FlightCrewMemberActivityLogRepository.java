
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLogs.ActivityLog;
import acme.entities.flightAssignments.FlightAssignment;

@Repository
public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select al from ActivityLog al where al.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("select al from ActivityLog al where al.flightCrewMember.id = :flightCrewMemberId")
	Collection<ActivityLog> findActivityLogsByFlightCrewMemberId(int flightCrewMemberId);

	@Query("select al from ActivityLog al where al.leg.id = :legId")
	Collection<ActivityLog> findActivityLogsByLegId(int legId);
}
