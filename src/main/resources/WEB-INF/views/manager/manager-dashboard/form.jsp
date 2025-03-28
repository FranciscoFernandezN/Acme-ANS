<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<acme:form>
	<acme:input-textbox code="manager.manager-dashboard.form.label.rankingByYearsOfExperience" path="rankingByYearsOfExperience" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.yearsToRetire" path="yearsToRetire" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.ratioOfOnTimeLegs" path="ratioOfOnTimeLegs" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.ratioOfDelayedLegs" path="ratioOfDelayedLegs" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.mostPopularAirportOfFlights" path="mostPopularAirportOfFlights" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.lessPopularAirportOfFlights" path="lessPopularAirportOfFlights" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.numberOfOnTimeLegs" path="numberOfOnTimeLegs" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.numberOfCancelledLegs" path="numberOfCancelledLegs" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.numberOfDelayedLegs" path="numberOfDelayedLegs" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.numberOfLandedLegs" path="numberOfLandedLegs" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.averageCostOfFlights" path="averageCostOfFlights" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.minCostOfFlights" path="minCostOfFlights" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.maxCostOfFlights" path="maxCostOfFlights" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.stdDeviationCostOfFlights" path="stdDeviationCostOfFlights" readonly="true"/>
	
</acme:form>