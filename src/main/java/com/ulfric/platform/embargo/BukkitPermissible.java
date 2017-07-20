package com.ulfric.platform.embargo;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;

import com.ulfric.embargo.Allowance;

import java.util.Objects;
import java.util.Set;

final class BukkitPermissible extends PermissibleBase {

	private final com.ulfric.embargo.Entity entity;

	public BukkitPermissible(ServerOperator opable, com.ulfric.embargo.Entity entity) {
		super(opable);
		Objects.requireNonNull(opable, "opable");
		Objects.requireNonNull(entity, "entity");

		this.entity = entity;
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		return isPermissionSet(permission.getName());
	}

	@Override
	public boolean isPermissionSet(String node) {
		return entity.testPermission(node) != Allowance.UNKNOWN;
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return hasPermission(permission.getName());
	}

	@Override
	public boolean hasPermission(String node) {
		return entity.testPermission(node) == Allowance.ALLOWED;
	}

	@Override
	public void recalculatePermissions() {
		if (entity == null) {
			return;
		}

		entity.recalculate();
	}

	@SuppressWarnings("sync-override")
	@Override
	public void clearPermissions() {
		// TODO delete cached user
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		throw new UnsupportedOperationException();
	}

}