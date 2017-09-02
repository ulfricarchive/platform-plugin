package com.ulfric.platform.andrew.resolvers;

import com.ulfric.dragoon.application.Container;

public class BukkitResolversContainer extends Container {

	public BukkitResolversContainer() {
		install(PlayerResolver.class);
		install(WorldResolver.class);
	}

}