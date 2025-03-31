<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="assistance-agent.tracking-log.form.label.lastUpdateMoment" path="lastUpdateMoment" readonly="true"/>
	<acme:input-textbox code="assistance-agent.tracking-log.form.label.step" path="step"/>
	<acme:input-textbox code="assistance-agent.tracking-log.form.label.resolutionPercentage" path="resolutionPercentage"/>
	<acme:input-textarea code="assistance-agent.tracking-log.form.label.resolution" path="resolution"/>
	<acme:input-select code="assistance-agent.tracking-log.form.label.claim" path="claim" choices="${claim}"/>
	<acme:input-checkbox code="assistance-agent.tracking-log.form.label.isPublished" path="isPublished"/>


	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && isPublished == false}">			
			<acme:submit code="assistance-agent.tracking-log.form.button.update" action="/assistance-agent/tracking-log/update"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">		
			<acme:submit code="assistance-agent.tracking-log.form.button.create" action="/assistance-agent/tracking-log/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>