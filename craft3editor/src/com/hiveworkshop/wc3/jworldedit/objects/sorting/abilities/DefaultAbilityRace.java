package com.hiveworkshop.wc3.jworldedit.objects.sorting.abilities;

import com.hiveworkshop.wc3.jworldedit.objects.sorting.general.SortRace;
import com.hiveworkshop.wc3.resources.WEString;

public enum DefaultAbilityRace implements SortRace {
	HUMAN("human", "WESTRING_RACE_HUMAN"),
	ORC("orc", "WESTRING_RACE_ORC"),
	UNDEAD("undead", "WESTRING_RACE_UNDEAD"),
	NIGHTELF("nightelf", "WESTRING_RACE_NIGHTELF"),
	NEUTRAL_HOSTILE("creeps", "WESTRING_NEUTRAL_HOSTILE"),
	NEUTRAL_PASSIVE("demon", "WESTRING_NEUTRAL_PASSIVE"),
	OTHER("other", "WESTRING_RACE_OTHER");

	private final String keyString;
	private final String displayName;

	private DefaultAbilityRace(final String keyString, final String displayKey) {
		this.keyString = keyString;
		this.displayName = WEString.getString(displayKey);
	}

	@Override
	public String getKeyString() {
		return keyString;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
}