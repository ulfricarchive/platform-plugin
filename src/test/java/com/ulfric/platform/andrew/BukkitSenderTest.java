package com.ulfric.platform.andrew;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.bukkit.command.CommandSender;

import com.google.common.truth.Truth;

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