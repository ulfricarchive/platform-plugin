package com.ulfric.platform;

import com.ulfric.dragoon.application.Container;

import java.util.Objects;

public class PluginContainer extends Container {

	private Plugin plugin;

	public PluginContainer(Plugin plugin) {
		Objects.requireNonNull(plugin, "plugin");

		this.plugin = plugin;
	}

	@Override
	public final String getName() {
		return plugin.getName();
	}

}