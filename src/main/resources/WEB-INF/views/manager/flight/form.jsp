<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.flight.form.label.tag" path="tag"/>
	<acme:input-checkbox code="manager.flight.form.label.needsSelfTransfer" path="needsSelfTransfer"/>
	<acme:input-money code="manager.flight.form.label.cost" path="cost"/>
	<acme:input-textarea code="manager.flight.form.label.description" path="description"/>
	<jstl:if test="${ acme:anyOf(_command, 'show|update|delete|publish') }">
		<acme:input-textbox code="manager.flight.form.label.origin" path="origin" readonly="true"/>
		<acme:input-textbox code="manager.flight.form.label.destiny" path="destiny" readonly="true"/>
		<acme:input-textbox code="manager.flight.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
		<acme:input-textbox code="manager.flight.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
		<acme:input-textbox code="manager.flight.form.label.numberOfLayovers" path="numberOfLayovers" readonly="true"/>
	</jstl:if>
	
	<jstl:if test="${_command == 'show'}">
		<acme:input-textbox code="manager.flight.form.label.defaultCost" path="defaultCost" readonly="true"/>
	</jstl:if>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && isDraftMode == true}">
			<acme:submit code="manager.flight.form.button.update" action="/manager/flight/update"/>
			<acme:submit code="manager.flight.form.button.publish" action="/manager/flight/publish"/>
			<acme:submit code="manager.flight.form.button.delete" action="/manager/flight/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.flight.form.button.create" action="/manager/flight/create"/>
		</jstl:when>		
	</jstl:choose>
	
	<acme:button code="manager.flight.form.button.legs" action="/manager/leg/list?masterId=${id}"/>
	
</acme:form>