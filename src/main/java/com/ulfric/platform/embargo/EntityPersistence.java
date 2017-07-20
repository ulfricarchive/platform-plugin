package com.ulfric.platform.embargo;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.StringUtils;

import com.ulfric.data.database.Data;
import com.ulfric.embargo.Allowance;
import com.ulfric.embargo.Entity;
import com.ulfric.embargo.Group;
import com.ulfric.embargo.limit.IntegerLimit;
import com.ulfric.embargo.limit.Limit;
import com.ulfric.embargo.limit.StandardLimits;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

final class EntityPersistence {

	private final Data data;
	private volatile boolean changedNodes;
	private volatile boolean changedLimits;
	private volatile boolean changedParents;

	public EntityPersistence(Data data) {
		this.data = data;
	}

	void changedNodes() {
		changedNodes = true;
	}

	void changedLimits() {
		changedLimits = true;
	}

	void changedParents() {
		changedParents = true;
	}

	public void loadNodes(PatriciaTrie<Allowance> permissions) {
		List<String> entries = data.getStringList("nodes");
		if (entries == null) {
			return;
		}

		for (String entry : entries) {
			if (entry.isEmpty()) {
				continue;
			}
			boolean negative = entry.charAt(0) == '-';
			if (negative) {
				entry = entry.substring(1);
			}
			permissions.put(entry, negative ? Allowance.DENIED : Allowance.ALLOWED);
		}
	}

	public void loadLimits(PatriciaTrie<Limit> limits) {
		Map<String, String> entries = data.getStringMap("limits");
		if (entries == null) {
			return;
		}

		entries.forEach((key, limitString) -> {
			if (key.isEmpty() || limitString.isEmpty()) {
				return;
			}

			Limit limit;
			boolean integer = limitString.startsWith("int/");
			if (integer) {
				limitString = limitString.substring("int/".length());
				limit = IntegerLimit.of(Integer.parseInt(limitString));
			} else {
				limit = StandardLimits.valueOf(limitString);
			}
			limits.put(key, limit);
		});
	}

	public void loadParents(Map<UUID, Entity> parents) {
		List<String> entries = data.getStringList("parents");
		if (entries == null) {
			return;
		}

		for (String entry : entries) {
			if (entry.isEmpty()) {
				continue;
			}
			if (entry.startsWith("group/")) {
				entry = entry.substring("group/".length());
			}
			Group group = PersistentGroup.getPersistentGroup(entry); // TODO validate not self
			if (group == null) {
				continue;
			}
			parents.put(group.getUniqueId(), group);
		}
	}

	public void saveNodes(PatriciaTrie<Allowance> permissions) {
		if (!changedNodes) {
			return;
		}
		changedNodes = false;

		if (permissions.isEmpty()) {
			data.delete("nodes");
			return;
		}

		List<String> entries = permissions.entrySet().stream().map(entry -> {
			if (entry.getValue() == Allowance.DENIED) {
				return '-' + entry.getKey();
			}
			return entry.getKey();
		}).filter(StringUtils::isNotBlank).collect(Collectors.toList());

		data.set("nodes", entries);
	}

	public void saveLimits(PatriciaTrie<Limit> limits) {
		if (!changedLimits) {
			return;
		}
		changedLimits = false;

		if (limits.isEmpty()) {
			data.delete("limits");
			return;
		}

		Map<String, String> entries = new LinkedHashMap<>();
		limits.forEach((key, limit) -> {
			String value;
			if (limit instanceof IntegerLimit) {
				IntegerLimit integerLimit = (IntegerLimit) limit;
				value = "int/" + integerLimit.intValue();
			} else {
				value = limit.toString();
			}
			entries.put(key, value);
		});

		data.set("limits", entries);
	}

	public void saveParents(Map<UUID, Entity> parents) {
		if (!changedParents) {
			return;
		}
		changedParents = false;

		if (parents.isEmpty()) {
			data.delete("parents");
			return;
		}

		List<String> entries = parents.values().stream().map(entry -> {
			if (entry instanceof Group) {
				return "group/" + entry.getName();
			}
			return null;
		}).filter(StringUtils::isNotBlank).collect(Collectors.toList());

		data.set("parents", entries);
	}

}