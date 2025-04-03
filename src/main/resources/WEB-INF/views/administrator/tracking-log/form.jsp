<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.tracking-log.form.label.lastUpdateMoment" path="lastUpdateMoment"/>
	<acme:input-textbox code="administrator.tracking-log.form.label.step" path="step"/>
	<acme:input-textbox code="administrator.tracking-log.form.label.resolutionPercentage" path="resolutionPercentage"/>
	<acme:input-textarea code="administrator.tracking-log.form.label.resolution" path="resolution"/>
	<acme:input-checkbox code="administrator.tracking-log.form.label.isPublished" path="isPublished"/>
</acme:form>