<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="assistance-agent.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
	<acme:input-email code="assistance-agent.claim.form.label.passengerEmail" path="passengerEmail"/>
	<acme:input-textarea code="assistance-agent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistance-agent.claim.form.label.claimType" path="claimType" choices="${claimType}"/>
	<acme:input-select code="assistance-agent.claim.form.label.indicator" path="indicator" choices="${indicator}" readonly="${_command == 'create'}"/>
	<acme:input-select code="assistance-agent.claim.form.label.leg" path="leg" choices="${leg}"/>
	<acme:input-checkbox code="assistance-agent.claim.form.label.isPublished" path="isPublished"/>

	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && isPublished == false}">			
			<acme:submit code="assistance-agent.claim.form.button.update" action="/assistance-agent/claim/update"/>
			<acme:submit code="assistance-agent.claim.form.button.delete" action="/assistance-agent/claim/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">		
			<acme:submit code="assistance-agent.claim.form.button.create" action="/assistance-agent/claim/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>