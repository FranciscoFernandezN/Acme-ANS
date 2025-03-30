<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column code="administrator.airline.list.label.name" path="name" width="30%"/>
    <acme:list-column code="administrator.airline.list.label.iATACode" path="iATACode" width="10%"/>
    <acme:list-column code="administrator.airline.list.label.type" path="type" width="15%"/>
    <acme:list-column code="administrator.airline.list.label.foundationMoment" path="foundationMoment" width="15%"/>
    <acme:list-column code="administrator.airline.list.label.website" path="website" width="20%"/>
    <acme:list-column code="administrator.airline.list.label.contactNumber" path="contactNumber" width="10%"/>
</acme:list>

<acme:button code="administrator.airline.list.button.create" action="/administrator/airline/create"/>
	