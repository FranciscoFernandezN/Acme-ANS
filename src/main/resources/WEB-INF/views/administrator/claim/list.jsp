<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.claim.list.label.registrationMoment" path="registrationMoment" width="10%"/>
	<acme:list-column code="administrator.claim.list.label.passengerEmail" path="passengerEmail" width="10%"/>
	<acme:list-column code="administrator.claim.list.label.description" path="description" width="10%"/>
	<acme:list-column code="administrator.claim.list.label.claimType" path="claimType" width="10%"/>
	<acme:list-column code="administrator.claim.list.label.indicator" path="indicator" width="10%"/>
	<acme:list-column code="administrator.claim.list.label.leg" path="leg" width="10%"/>
	<acme:list-column code="administrator.claim.list.label.isPublished" path="isPublished" width="10%"/>
</acme:list>