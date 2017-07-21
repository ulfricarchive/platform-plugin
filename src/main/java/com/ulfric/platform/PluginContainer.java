package com.ulfric.platform;

import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.extension.loader.Loader;

import java.util.Objects;

@Loader
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

	@Override
	protected void log(String message) {
	}

}