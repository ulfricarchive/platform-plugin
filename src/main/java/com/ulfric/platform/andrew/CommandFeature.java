package com.ulfric.platform.andrew;

import com.ulfric.andrew.Command;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Feature;
import com.ulfric.dragoon.extension.inject.Inject;

public class CommandFeature extends Feature {

	@Inject
	private ObjectFactory factory;

	@Override
	public Application apply(Object object) {
		if (object instanceof Command) {
			return factory.request(CommandApplication.class, object);
		}
		return null;
	}

}
