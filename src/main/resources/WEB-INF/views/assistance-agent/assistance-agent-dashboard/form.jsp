<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="assistance-agent.assistance-agent-dashboard.form.label.ratioOfClaimsResolved" path="ratioOfClaimsResolved" readonly="true"/>
	<acme:input-textbox code="assistance-agent.assistance-agent-dashboard.form.label.ratioOfClaimsRejected" path="ratioOfClaimsRejected" readonly="true"/>
	<acme:input-textbox code="assistance-agent.assistance-agent-dashboard.form.label.monthHigherNumClaims" path="monthHigherNumClaims" readonly="true"/>
	<acme:input-textbox code="assistance-agent.assistance-agent-dashboard.form.label.averageLogsOfClaims" path="averageLogsOfClaims" readonly="true"/>
	<acme:input-textbox code="assistance-agent.assistance-agent-dashboard.form.label.minLogsOfClaims" path="minLogsOfClaims" readonly="true"/>
	<acme:input-textbox code="assistance-agent.assistance-agent-dashboard.form.label.maxLogsOfClaims" path="maxLogsOfClaims" readonly="true"/>
	<acme:input-textbox code="assistance-agent.assistance-agent-dashboard.form.label.stdDeviationLogsOfClaims" path="stdDeviationLogsOfClaims" readonly="true"/>
	<acme:input-textbox code="assistance-agent.assistance-agent-dashboard.form.label.numberOfClaimsLastMonth" path="numberOfClaimsLastMonth" readonly="true"/>
</acme:form>