<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="administrator.airline.form.label.name" path="name"  placeholder="administrator.airline.form.placeholder.name"/>
    <acme:input-textbox code="administrator.airline.form.label.iATACode" path="iATACode" placeholder="administrator.airline.form.placeholder.iATACode"/>
    <acme:input-url code="administrator.airline.form.label.website" path="website" placeholder="administrator.airline.form.placeholder.website"/>
    <acme:input-select code="administrator.airline.form.label.type" path="type" choices="${type}"/>
    <acme:input-date code="administrator.airline.form.label.foundationMoment" path="foundationMoment"/>
    <acme:input-textbox code="administrator.airline.form.label.email" path="email" placeholder="administrator.airline.form.placeholder.email"/>
    <acme:input-textbox code="administrator.airline.form.label.contactNumber" path="contactNumber" placeholder="administrator.airline.form.placeholder.contactNumber"/>
	<acme:input-checkbox code="administrator.airport.form.label.confirmation" path="confirmation"/>

	<jstl:choose>
			<jstl:when test="${acme:anyOf(_command, 'show|update')}">
				<acme:submit code="administrator.airline.form.button.update" action="/administrator/airline/update"/>
			</jstl:when>
			<jstl:when test="${_command == 'create'}">
				<acme:submit code="administrator.airline.form.button.create" action="/administrator/airline/create"/>
			</jstl:when>		
		</jstl:choose>
</acme:form>

