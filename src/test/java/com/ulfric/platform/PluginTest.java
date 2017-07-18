package com.ulfric.platform;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.botch.Botch;
import com.ulfric.platform.PluginTest.TestPlugin;

@RunWith(JUnitPlatform.class)
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