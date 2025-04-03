<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="flight-crew-member.activity-log.form.label.registrationMoment" path="registrationMoment"/>
	<acme:input-select code="flight-crew-member.activity-log.form.label.leg" path="leg" choices="${legs}"/>
	<acme:input-select code="flight-crew-member.activity-log.form.label.flightCrewMember" path="flightCrewMember" choices="${flightCrewMembers}" />
	<acme:input-textbox code="flight-crew-member.activity-log.form.label.typeOfIncident" path="typeOfIncident" />
	<acme:input-textbox code="flight-crew-member.activity-log.form.label.description" path="description"/>
	<acme:input-textbox code="flight-crew-member.activity-log.form.label.severityLevel" path="severityLevel" />
	<acme:input-checkbox code="flight-crew-member.activity-log.form.label.confirmation" path="confirmation"/>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command,'show|update|delete|publish') && isDraftMode == true}">
			<acme:submit code="flight-crew-member.activity-log.form.button.update" action="/flight-crew-member/activity-log/update"/>
			<acme:submit code="flight-crew-member.activity-log.form.button.delete" action="/flight-crew-member/activity-log/delete"/>
			<acme:submit code="flight-crew-member.activity-log.form.button.publish" action="/flight-crew-member/activity-log/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="flight-crew-member.activity-log.form.button.create" action="/flight-crew-member/activity-log/create"/>
		</jstl:when>
	</jstl:choose>
</acme:form>