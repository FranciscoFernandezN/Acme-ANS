<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.supported-currency.form.label.currencyName" path="currencyName" placeholder="administrator.supported-currency.form.placeholder.currencyName"/>
	<jstl:if test="${ acme:anyOf(_command, 'show|update') }">
		<acme:input-textbox code="administrator.supported-currency.form.label.isDefaultCurrency" path="isDefaultCurrency" readonly="true"/>
	</jstl:if>
	
	
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update') && isDefaultCurrency == false}">
			<acme:submit code="administrator.supported-currency.form.button.update" action="/administrator/supported-currency/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="administrator.supported-currency.form.button.create" action="/administrator/supported-currency/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>