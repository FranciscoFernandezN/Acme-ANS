
package acme.features.flightCrewMember.flightAssignment;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignments.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.FlightCrewMember;

@Repository
public interface FlightCrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("Select f from FlightCrewMember f")
	List<FlightCrewMember> findAllFlightCrewMembers();

	@Query("Select l from Leg l")
	List<Leg> findAllLegs();

	@Query("Select f from FlightCrewMember f where f.id = :flightCrewMemberId")
	FlightCrewMember findFlightCrewMemberById(int flightCrewMemberId);

	@Query("Select l from Leg l where l.id = :legId")
	Leg findLegById(int legId);

	@Query("Select distinct a.leg from FlightAssignment a where a.flightCrewMember.id = :flightCrewMemberId")
	List<Leg> findLegsByFlightCrewMemberId(int flightCrewMemberId);

	@Query("Select fa from FlightAssignment fa where fa.id = :flightAssignmentId")
	FlightAssignment findFlightAssignmentById(int flightAssignmentId);

	@Query("Select fa from FlightAssignment fa where fa.leg.scheduledArrival < :date")
	List<FlightAssignment> findFlightAssignmentBeforeCurrent(Date date);

	@Query("Select fa from FlightAssignment fa where fa.leg.scheduledArrival > :date")
	List<FlightAssignment> findFlightAssignmentAfterCurrent(Date date);
}
