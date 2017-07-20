package com.ulfric.platform.embargo;

import com.ulfric.data.database.Data;
import com.ulfric.embargo.Allowance;
import com.ulfric.embargo.Entity;
import com.ulfric.embargo.User;
import com.ulfric.embargo.limit.Limit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PersistentUser extends User {

	static final List<PersistentUser> ALL_USERS = new ArrayList<>();

	static PersistentUser getUser(UUID uniqueId, Supplier<PersistentUser> compute) {
		User user = User.getUser(uniqueId);
		if (user instanceof PersistentUser) {
			return (PersistentUser) user;
		}
		return compute.get();
	}

	private final EntityPersistence data;

	public PersistentUser(Data data, String name, UUID uniqueId) {
		super(name, uniqueId);

		this.data = new EntityPersistence(data);
		load();
		ALL_USERS.add(this);
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
