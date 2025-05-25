<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.passenger.list.label.fullName" path="fullName" width="20%"/>
	<acme:list-column code="administrator.passenger.list.label.email" path="email" width="20%"/>
	<acme:list-column code="administrator.passenger.list.label.passportNumber" path="passportNumber" width="20%"/>
	<acme:list-column code="administrator.passenger.list.label.dateOfBirth" path="dateOfBirth" width="20%"/>
	<acme:list-column code="administrator.passenger.list.label.specialNeeds" path="specialNeeds" width="20%"/>
	<jstl:if test="${banned}">
		<acme:list-column code="administrator.passenger.list.label.nationality" path="nationality" width="20%"/>
	</jstl:if>
</acme:list>

<acme:button code="administrator.passenger.list.button.create-ban" action="/administrator/ban/create"/>