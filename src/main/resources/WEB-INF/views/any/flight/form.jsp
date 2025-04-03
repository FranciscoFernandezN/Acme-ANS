<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<acme:input-textbox code="any.flight.form.label.id" path="id" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.tag" path="tag" readonly="true"/>
	<acme:input-checkbox code="any.flight.form.label.needsSelfTransfer" path="needsSelfTransfer" readonly="true"/>
	<acme:input-money code="any.flight.form.label.cost" path="cost" readonly="true"/>
	<acme:input-textarea code="any.flight.form.label.description" path="description" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.origin" path="origin" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.destiny" path="destiny" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.numberOfLayovers" path="numberOfLayovers" readonly="true"/>
	
	<acme:button code="any.flight.form.button.legs" action="/any/leg/list?masterId=${id}"/>
	
</acme:form>