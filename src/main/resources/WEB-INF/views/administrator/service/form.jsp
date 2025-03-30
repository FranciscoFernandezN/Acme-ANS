<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.service.form.label.name" path="name"/>
	<acme:input-textbox code="administrator.service.form.label.picture" path="picture"/>
	<acme:input-textbox code="administrator.service.form.label.avgDwellTime" path="avgDwellTime"/>
	<acme:input-textbox code="administrator.service.form.label.promotionCode" path="promotionCode"/>
	<acme:input-money code="administrator.service.form.label.money" path="money"/>
	<acme:input-select code="administrator.service.form.label.iATACode" path="iATACode" choices="${airportChoices}"/>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && isDraftMode == true}">
			<acme:submit code="administrator.service.form.button.update" action="/administrator/service/update"/>
			<acme:submit code="administrator.service.form.button.delete" action="/administrator/service/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="administrator.service.form.button.create" action="/administrator/service/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>