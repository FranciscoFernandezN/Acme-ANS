<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistance-agent.tracking-log.list.label.lastUpdateMoment" path="lastUpdateMoment" width="10%"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.step" path="step" width="10%"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.resolutionPercentage" path="resolutionPercentage" width="10%"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.resolution" path="resolution" width="10%"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.claim" path="claim" width="10%"/>
	<acme:list-column code="assistance-agent.tracking-log.list.label.isPublished" path="isPublished" width="10%"/>
</acme:list>

<acme:button code="assistance-agent.tracking-log.list.button.create" action="/assistance-agent/tracking-log/create"/>