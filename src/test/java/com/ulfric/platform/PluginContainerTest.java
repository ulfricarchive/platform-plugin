package com.ulfric.platform;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import com.ulfric.botch.Botch;
import com.ulfric.dragoon.application.Container;
import com.ulfric.plugin.Plugin;
import com.ulfric.plugin.PluginContainer;

class PluginContainerTest extends Botch<Plugin> {

	public PluginContainerTest() {
		super(Plugin.class);
	}

	@Test
	void testGetNameDelegates() {
		Container container = new PluginContainer(plugin);
		Truth.assertThat(container.getName()).isEqualTo(plugin.getName().toLowerCase());
	}

}
