package com.ulfric.platform;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.google.common.truth.Truth;

import com.ulfric.botch.Botch;
import com.ulfric.dragoon.application.Container;

@RunWith(JUnitPlatform.class)
class PluginContainerTest extends Botch<Plugin> {

	public PluginContainerTest() {
		super(Plugin.class);
	}

	@Test
	void testGetNameDelegates() {
		Container container = new PluginContainer(plugin);
		Truth.assertThat(container.getName()).isEqualTo(plugin.getName());
	}

}
