<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="authenticated.assistance-agent.form.label.employeeCode" path="employeeCode"/>
	<acme:input-textbox code="authenticated.assistance-agent.form.label.languages" path="languages"/>
	<acme:input-moment code="authenticated.assistance-agent.form.label.firstWorkingDate" path="firstWorkingDate"/>
	<acme:input-textarea code="authenticated.assistance-agent.form.label.biography" path="biography"/>
	<acme:input-textbox code="authenticated.assistance-agent.form.label.salary" path="salary" placeholder="authenticated.assistance-agent.form.placeholder.salary"/>
	<acme:input-url code="authenticated.assistance-agent.form.label.photoLink" path="photoLink"/>
	<acme:input-select code="authenticated.assistance-agent.form.label.airline" path="airline" choices="${ airlineIATACodes }"/>
	
	<jstl:if test="${_command == 'create'}">
		<acme:submit code="authenticated.assistance-agent.form.button.create" action="/authenticated/assistance-agent/create"/>
	</jstl:if>
	
	<jstl:if test="${_command == 'update'}">
		<acme:submit code="authenticated.assistance-agent.form.button.update" action="/authenticated/assistance-agent/update"/>
	</jstl:if>
</acme:form>
