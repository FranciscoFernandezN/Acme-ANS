
package acme.features.flightCrewMember.activitylog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylogs.ActivityLog;
import acme.entities.flightassignments.FlightAssignment;

@Repository
public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("SELECT a FROM ActivityLog a WHERE a.flightAssignment.flightCrewMember.id = :id")
	Collection<ActivityLog> findAllActivityLogsByFlightCrewMemberId(int id);

	@Query("SELECT a FROM ActivityLog a WHERE a.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("SELECT f FROM FlightAssignment f WHERE f.flightCrewMember.id = :crewMemberId")
	Collection<FlightAssignment> findFlightAssignmentsByFlightCrewMember(int crewMemberId);
}
