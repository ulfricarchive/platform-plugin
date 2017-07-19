package com.ulfric.platform.andrew;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

import com.ulfric.andrew.Command;
import com.ulfric.andrew.Registry;
import com.ulfric.dragoon.ObjectFactory;

@RunWith(JUnitPlatform.class)
class CommandFeatureTest {

	private ObjectFactory factory;
	private CommandFeature wrapper;
	private Registry registry;

	@BeforeEach
	void setup() {
		factory = new ObjectFactory();
		registry = Mockito.mock(Registry.class);
		factory.bind(Registry.class).toValue(registry);
		wrapper = factory.request(CommandFeature.class);
	}

	@Test
	void testWrapCommand() {
		Command raw = Mockito.mock(Command.class);
		Truth.assertThat(wrapper.apply(raw)).isNotNull();
	}

	@Test
	void testWrapNull() {
		Truth.assertThat(wrapper.apply(null)).isNull();
	}

}