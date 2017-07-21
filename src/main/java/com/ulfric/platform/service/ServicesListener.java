package com.ulfric.platform.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class ServicesListener implements Listener {

	@EventHandler
	public void on(PluginEnableEvent event) {
		Services.load(event.getPlugin());
	}

}