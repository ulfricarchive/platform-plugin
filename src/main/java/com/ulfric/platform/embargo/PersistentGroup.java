package com.ulfric.platform.embargo;

import com.ulfric.data.database.Data;
import com.ulfric.data.database.Store;
import com.ulfric.embargo.Allowance;
import com.ulfric.embargo.Entity;
import com.ulfric.embargo.Group;
import com.ulfric.embargo.limit.Limit;

import java.util.ArrayList;
import java.util.List;

public class PersistentGroup extends Group {

	static final List<PersistentGroup> ALL_GROUPS = new ArrayList<>();

	static PersistentGroup getPersistentGroup(String name) {
		Group group = Group.getGroup(name);
		if (group instanceof PersistentGroup) {
			return (PersistentGroup) group;
		}
		Store database = EmbargoContainer.getGroupsDatabase();
		if (database == null) {
			return null;
		}
		return new PersistentGroup(database.getData(name), name);
	}

	private final EntityPersistence data;

	public PersistentGroup(Data data, String name) {
		super(name);

		this.data = new EntityPersistence(data);
		load();
		ALL_GROUPS.add(this);
	}

	@Override
	public void setPermission(String node, Allowance allowance) {
		super.setPermission(node, allowance);
		data.changedNodes();
	}

	@Override
	public void clearPermission(String node) {
		super.clearPermission(node);
		data.changedNodes();
	}

	@Override
	public void setLimit(String node, Limit limit) {
		super.setLimit(node, limit);
		data.changedLimits();
	}

	@Override
	public void clearLimit(String node) {
		super.clearLimit(node);
		data.changedLimits();
	}

	@Override
	public void addParent(Entity entity) {
		super.addParent(entity);
		data.changedParents();
	}

	@Override
	public void removeParent(Entity entity) {
		super.removeParent(entity);
		data.changedParents();
	}

	private void load() {
		data.loadNodes(this.permissions);
		data.loadLimits(this.limits);
		data.loadParents(this.parents);
	}

	public void write() {
		data.saveNodes(this.permissions);
		data.saveLimits(this.limits);
		data.saveParents(this.parents);
	}

}
