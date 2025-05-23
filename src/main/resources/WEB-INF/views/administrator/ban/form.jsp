<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select code="administrator.ban.form.label.passenger" path="passenger" choices="${passengerChoices}" readonly="${acme:anyOf(_command, 'show|update')}"/>
	<acme:input-textbox code="administrator.ban.form.label.reasonForBan" path="reasonForBan"/>
	<jstl:if test="${_command == 'create'}">
		<acme:input-textbox code="administrator.ban.form.label.nationality" path="nationality"/>
	</jstl:if>
	<acme:input-moment code="administrator.ban.form.label.banIssuedDate" path="banIssuedDate" readonly="true"/>
	<acme:input-moment code="administrator.ban.form.label.liftDate" path="liftDate"/>
	<acme:input-checkbox code="administrator.ban.form.label.confirm" path="confirm"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:submit code="administrator.ban.form.button.update" action="/administrator/ban/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="administrator.ban.form.button.create" action="/administrator/ban/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>


