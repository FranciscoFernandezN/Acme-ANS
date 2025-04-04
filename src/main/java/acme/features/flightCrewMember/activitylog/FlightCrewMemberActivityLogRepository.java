
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLogs.ActivityLog;
import acme.entities.flightAssignments.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.FlightCrewMember;

@Repository
public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("Select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("Select al from ActivityLog al where al.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("Select al from ActivityLog al where al.flightCrewMember.id = :flightCrewMemberId")
	Collection<ActivityLog> findActivityLogsByFlightCrewMemberId(int flightCrewMemberId);

	@Query("Select al from ActivityLog al where al.leg.id = :legId")
	Collection<ActivityLog> findActivityLogsByLegId(int legId);

	@Query("Select al from ActivityLog al")
	Collection<ActivityLog> findAllActivityLogs();

	@Query("Select f from FlightCrewMember f")
	List<FlightCrewMember> findAllFlightCrewMembers();

	@Query("Select l from Leg l")
	List<Leg> findAllLegs();

	@Query("Select f from FlightCrewMember f where f.id = :flightCrewMemberId")
	FlightCrewMember findFlightCrewMemberById(int flightCrewMemberId);

	@Query("Select l from Leg l where l.id = :legId")
	Leg findLegById(int legId);
}
