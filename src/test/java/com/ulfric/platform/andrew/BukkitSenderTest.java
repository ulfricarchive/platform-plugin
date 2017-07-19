package com.ulfric.platform.andrew;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.bukkit.command.CommandSender;

import com.google.common.truth.Truth;

@RunWith(JUnitPlatform.class)
class BukkitSenderTest {

	@Test
	void testGetHandleNull() {
		Truth.assertThat(BukkitSender.getHandle(null)).isNull();
	}

	@Test
	void testGetHandleBukkitSender() {
		Truth.assertThat(BukkitSender.getHandle(new BukkitSender(Mockito.mock(CommandSender.class)))).isNotNull();
	}

}