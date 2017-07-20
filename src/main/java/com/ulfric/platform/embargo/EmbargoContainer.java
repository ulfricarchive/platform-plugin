package com.ulfric.platform.embargo;

import com.ulfric.data.database.Store;
import com.ulfric.data.database.Database;
import com.ulfric.dragoon.application.Container;

public class EmbargoContainer extends Container {

	private static Store groupsDatabase;

	static Store getGroupsDatabase() {
		return groupsDatabase;
	}

	@Database
	private Store groups;

	public EmbargoContainer() {
		install(EmbargoListener.class);

		addBootHook(this::loadGroups);
		addShutdownHook(this::saveEntities);
	}

	private void loadGroups() {
		groupsDatabase = groups;
	}

	private void saveEntities() { // TODO log sizes
		PersistentUser.ALL_USERS.parallelStream().forEach(PersistentUser::write);
		PersistentGroup.ALL_GROUPS.parallelStream().forEach(PersistentGroup::write);
	}

}