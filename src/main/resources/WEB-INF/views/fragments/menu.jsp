<%--
- menu.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.favourite-link" action="http://www.example.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.fran-favourite-link" action="https://www.reddit.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.trabuco-favourite-link" action="https://zonatmo.com/library/manga/8399/berserk"/>
			<acme:menu-suboption code="master.menu.anonymous.bea-favourite-link" action="https://minecraft.fandom.com/wiki/Minecraft_Wiki"/>
			<acme:menu-suboption code="master.menu.anonymous.juavarver-favourite-link" action="https://www.marca.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.estegn10-favourite-link" action="https://aternos.org/"/>
			
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.review">
			<acme:menu-suboption code="master.menu.any.list-reviews" action="/any/review/list" />
			<acme:menu-suboption code="master.menu.any.create-reviews" action="/any/review/create" />
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.flight">
			<acme:menu-suboption code="master.menu.any.list-flights" action="/any/flight/list" />
			<acme:menu-suboption code="master.menu.any.list-bad-weather-flights" action="/any/flight/list-bad-weather"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.weather">
			<acme:menu-suboption code="master.menu.any.list-weather" action="/any/weather/list" />
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.airport">
			<acme:menu-suboption code="master.menu.authenticated.list-airport" action="/any/airport/list" />
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.service">
			<acme:menu-suboption code="master.menu.any.list-services" action="/any/service/list" />
		</acme:menu-option>

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-airlines"  action="/administrator/airline/list" />
			<acme:menu-suboption code="master.menu.administrator.create-airlines"  action="/administrator/airline/create" />
      <acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-aircrafts" action="/administrator/aircraft/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-bookings" action="/administrator/booking/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-services" action="/administrator/service/list"/>
			<acme:menu-suboption code="master.menu.administrator.create-services" action="/administrator/service/create"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-supported-currencies" action="/administrator/supported-currency/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-weather" action="/administrator/weather/populate"/>
      <acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-claims" action="/administrator/claim/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-airports" action="/administrator/airport/list"/>
			<acme:menu-suboption code="master.menu.administrator.create-airports" action="/administrator/airport/create"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.assistance-agent" access="hasRealm('AssistanceAgent')">
			<acme:menu-suboption code="master.menu.assistance-agent.dashboard" action="/assistance-agent/assistance-agent-dashboard/show" />
      <acme:menu-separator/>
			<acme:menu-suboption code="master.menu.assistance-agent.list-complete-claims" action="/assistance-agent/claim/list-complete" />
			<acme:menu-suboption code="master.menu.assistance-agent.list-in-progress-claims" action="/assistance-agent/claim/list-in-progress" />
			<acme:menu-suboption code="master.menu.assistance-agent.create-claims" action="/assistance-agent/claim/create" />
      <acme:menu-separator/>
			<acme:menu-suboption code="master.menu.assistance-agent.list-tracking-logs" action="/assistance-agent/tracking-log/list" />
			<acme:menu-suboption code="master.menu.assistance-agent.create-tracking-logs" action="/assistance-agent/tracking-log/create" />
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.flight-crew-member" access="hasRealm('FlightCrewMember')">
			<acme:menu-suboption code="master.menu.flight-crew-member.list-before-current" action="/flight-crew-member/flight-assignment/list-before-current"/>
			<acme:menu-suboption code="master.menu.flight-crew-member.list-after-current" action="/flight-crew-member/flight-assignment/list-after-current"/>
						<acme:menu-suboption code="master.menu.flight-crew-member.list-activity-log" action="/flight-crew-member/activity-log/list"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.manager" access="hasRealm('Manager')">
			<acme:menu-suboption code="master.menu.manager.dashboard" action="/manager/manager-dashboard/show"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.manager.list-legs" action="/manager/leg/list"/>
			<acme:menu-suboption code="master.menu.manager.create-legs" action="/manager/leg/create"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.manager.list-flights" action="/manager/flight/list"/>
			<acme:menu-suboption code="master.menu.manager.create-flights" action="/manager/flight/create"/>
		</acme:menu-option>
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
			<acme:menu-suboption code="master.menu.customer.dashboard" action="/customer/customer-dashboard/show"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.customer.list-booking" action="/customer/booking/list"/>
			<acme:menu-suboption code="master.menu.customer.create-booking" action="/customer/booking/create"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.customer.list-passenger" action="/customer/passenger/list"/>
			<acme:menu-suboption code="master.menu.customer.create-passenger" action="/customer/passenger/create"/>
		</acme:menu-option>
	</acme:menu-left>

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-manager" action="/authenticated/manager/create" access="!hasRealm('Manager')"/>
			<acme:menu-suboption code="master.menu.user-account.manager-profile" action="/authenticated/manager/update" access="hasRealm('Manager')"/>
      <acme:menu-suboption code="master.menu.user-account.become-customer" action="/authenticated/customer/create" access="!hasRealm('Customer')"/>
			<acme:menu-suboption code="master.menu.user-account.customer-profile" action="/authenticated/customer/update" access="hasRealm('Customer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-agent" action="/authenticated/assistance-agent/create" access="!hasRealm('AssistanceAgent')"/>
			<acme:menu-suboption code="master.menu.user-account.agent-profile" action="/authenticated/assistance-agent/update" access="hasRealm('AssistanceAgent')"/>
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>

