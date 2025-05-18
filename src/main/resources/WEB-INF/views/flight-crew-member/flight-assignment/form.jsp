<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.duty" path="duty" choices="${duties}"/>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.leg" path="leg" choices="${legs}"/>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.flightCrewMember" path="flightCrewMember" choices="${flightCrewMembers}" />
	<acme:input-moment code="flight-crew-member.flight-assignment.form.label.lastUpDate" path="lastUpDate" readonly="true" />
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.currentStatus" path="currentStatus" choices="${currentStatus}"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.remarks" path="remarks" />
	<acme:input-checkbox code="flight-crew-member.flight-assignment.form.label.confirmation" path="confirmation"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command,'show|update|delete|publish') && isDraftMode == true && isAvailable == true}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command,'create') && isAvailable == true}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
		</jstl:when>
	</jstl:choose>
</acme:form>
