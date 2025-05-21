<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.ban.list.label.banIssuedDate" path="banIssuedDate" width="20%"/>
	<acme:list-column code="administrator.ban.list.label.liftDate" path="liftDate" width="20%"/>
	<acme:list-column code="administrator.ban.list.label.reasonForBan" path="reasonForBan" width="20%"/>
</acme:list>