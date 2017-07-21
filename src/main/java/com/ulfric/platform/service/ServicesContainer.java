package com.ulfric.platform.service;

import com.ulfric.dragoon.application.Container;

public class ServicesContainer extends Container {

	public ServicesContainer() {
		install(ServicesListener.class);
	}

}