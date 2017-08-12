package com.ulfric.platform;

import org.junit.jupiter.api.Test;

import com.ulfric.botch.Botch;
import com.ulfric.platform.PluginTest.TestPlugin;
import com.ulfric.plugin.Plugin;

class PluginTest extends Botch<TestPlugin> {

	public PluginTest() {
		super(TestPlugin.class);
	}

	@Test
	void testLoadDoesNothing() {
		plugin.onLoad();
	}

	public static class TestPlugin extends Plugin {
	}

}