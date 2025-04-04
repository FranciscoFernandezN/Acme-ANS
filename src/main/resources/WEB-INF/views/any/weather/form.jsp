<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.weather.form.label.forecastDate" path="forecastDate" />
	<acme:input-textbox code="any.weather.form.label.city" path="city" />
	<acme:input-textbox code="any.weather.form.label.status" path="status"/>
	<acme:input-textbox code="any.weather.form.label.temperature" path="temperature" />
	<acme:input-integer code="any.weather.form.label.visibility" path="visibility"/>
	<acme:input-textbox code="any.weather.form.label.rainPerHour" path="rainPerHour"/>
	<acme:input-textbox code="any.weather.form.label.snowPerHour" path="snowPerHour"/>
	<acme:input-textbox code="any.weather.form.label.wind" path="wind"/>
</acme:form>