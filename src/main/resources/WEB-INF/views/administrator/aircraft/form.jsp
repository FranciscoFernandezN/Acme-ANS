<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	<acme:input-textbox code="aircraft.form.label.model" path="model"/>
	<acme:input-textbox code="aircraft.form.label.registrationNumber" path="registrationNumber"/>
	<acme:input-integer code="aircraft.form.label.capacity" path="capacity"/>
	<acme:input-integer code="aircraft.form.label.cargoWeight" path="cargoWeight"/>
	
	<acme:input-select code="aircraft.form.label.status" path="status" choices="ACTIVE,UNDER_MAINTENANCE"/>
	
	<acme:input-textarea code="aircraft.form.label.details" path="details"/>
	
	<jstl:if test="${confirmation == 'N/A'}">
		<acme:input-checkbox code="aircraft.form.label.confirmation" path="confirmation"/>
		<acme:submit code="aircraft.form.button.save" action="/any/aircraft/create"/>
	</jstl:if>		
</acme:form>
