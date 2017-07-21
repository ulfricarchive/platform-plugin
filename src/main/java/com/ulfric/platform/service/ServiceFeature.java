package com.ulfric.platform.service;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Feature;

public class ServiceFeature extends Feature {

	@Override
	public Application apply(Object service) {
		return service instanceof Service ? new ServiceApplication((Service) service) : null;
	}

}