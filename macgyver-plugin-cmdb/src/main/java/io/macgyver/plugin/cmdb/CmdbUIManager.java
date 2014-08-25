package io.macgyver.plugin.cmdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.macgyver.core.web.vaadin.MacGyverUI;
import io.macgyver.core.web.vaadin.MacGyverUICreateEvent;
import io.macgyver.neo4j.rest.Neo4jRestClient;

import com.google.common.eventbus.Subscribe;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class CmdbUIManager {

	Logger logger = LoggerFactory.getLogger(CmdbUIManager.class);

	@Autowired
	Neo4jRestClient neo4j;

	
	
	@Subscribe
	public void onCreateEvent(MacGyverUICreateEvent event) {
		logger.info("onCreateEvent: {}",event);
		
		MacGyverUI ui = event.getUI();
		
		Navigator nav = ui.getNavigator();
		MenuBar bar = ui.getMenuBar();
		
	
		
		MenuItem inventory = bar.addItem("Inventory", null);
		inventory.addItem("App Instances", ui.navigateMenuCommand("cmdb/appInstances"));
		
		nav.addView("cmdb/appInstances", AppInstancesView.class);
	}
	
}
