<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.manager-dashboard.form.label.lastFiveDestinations" path="lastFiveDestinations" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.moneySpentLastYear" path="moneySpentLastYear" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.numOfBookingsByTravelClass" path="numOfBookingsByTravelClass" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.avgCostOfBookingsLastFiveYears" path="avgCostOfBookingsLastFiveYears" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.minCostOfBookingsLastFiveYears" path="minCostOfBookingsLastFiveYears" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.maxCostOfBookingsLastFiveYears" path="maxCostOfBookingsLastFiveYears" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.stdDeviationCostOfBookingsLastFiveYears" path="stdDeviationCostOfBookingsLastFiveYears" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.avgNumOfPassengersInBookings" path="avgNumOfPassengersInBookings" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.minNumOfPassengersInBookings" path="minNumOfPassengersInBookings" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.maxNumOfPassengersInBookings" path="maxNumOfPassengersInBookings" readonly="true"/>
	<acme:input-textbox code="manager.manager-dashboard.form.label.stdDeviationNumOfPassengersInBookings" path="stdDeviationNumOfPassengersInBookings" readonly="true"/>
</acme:form>