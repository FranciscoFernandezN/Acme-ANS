<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.aircraft.form.label.model" path="model" placeholder="administrator.aircraft.form.placeholder.model"/>
	<acme:input-textbox code="administrator.aircraft.form.label.registrationNumber" path="registrationNumber" placeholder="administrator.aircraft.form.placeholder.registrationNumber"/>
	<acme:input-textbox code="administrator.aircraft.form.label.capacity" path="capacity" placeholder="administrator.aircraft.form.placeholder.capacity"/>
	<acme:input-textbox code="administrator.aircraft.form.label.cargoWeight" path="cargoWeight" placeholder="administrator.aircraft.form.placeholder.cargoWeight"/>
	<acme:input-select code="administrator.aircraft.form.label.status" path="status" choices="${statuses}" />
	<acme:input-textbox code="administrator.aircraft.form.label.details" path="details" placeholder="administrator.aircraft.form.placeholder.details"/>
	<acme:input-select code="administrator.aircraft.form.label.airline" path="airline" choices="${airlines}"/>
	<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
	

	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:submit code="administrator.aircraft.form.button.update" action="/administrator/aircraft/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="administrator.aircraft.form.button.create" action="/administrator/aircraft/create"/>
		</jstl:when>		
	</jstl:choose>		
</acme:form>