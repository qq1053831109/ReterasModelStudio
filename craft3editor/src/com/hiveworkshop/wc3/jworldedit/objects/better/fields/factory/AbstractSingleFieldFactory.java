package com.hiveworkshop.wc3.jworldedit.objects.better.fields.factory;

import com.hiveworkshop.wc3.jworldedit.objects.better.fields.BooleanObjectField;
import com.hiveworkshop.wc3.jworldedit.objects.better.fields.EditableOnscreenObjectField;
import com.hiveworkshop.wc3.jworldedit.objects.better.fields.FloatObjectField;
import com.hiveworkshop.wc3.jworldedit.objects.better.fields.GameEnumObjectField;
import com.hiveworkshop.wc3.jworldedit.objects.better.fields.IntegerObjectField;
import com.hiveworkshop.wc3.jworldedit.objects.better.fields.StringObjectField;
import com.hiveworkshop.wc3.units.GameObject;
import com.hiveworkshop.wc3.units.ObjectData;
import com.hiveworkshop.wc3.units.StandardObjectData;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData.MutableGameObject;
import com.hiveworkshop.wc3.units.objectdata.MutableObjectData.WorldEditorDataType;
import com.hiveworkshop.wc3.units.objectdata.War3ID;

public abstract class AbstractSingleFieldFactory implements SingleFieldFactory {
	@Override
	public final EditableOnscreenObjectField create(final MutableGameObject gameObject, final ObjectData metaData,
			final War3ID metaKey, final int level, final WorldEditorDataType worldEditorDataType,
			final boolean hasMoreThanOneLevel) {
		final GameObject metaField = metaData.get(metaKey.toString());

		final String displayName = getDisplayName(metaData, metaKey, hasMoreThanOneLevel ? level : 0, gameObject);
		final String displayPrefix = getDisplayPrefix(metaData, metaKey, hasMoreThanOneLevel ? level : 0, gameObject);
		final String rawDataName = getRawDataName(metaData, metaKey, hasMoreThanOneLevel ? level : 0);
		final String metaDataType = metaField.getField("type");
		switch (metaDataType) {
		case "attackBits":
		case "teamColor":
		case "deathType":
		case "versionFlags":
		case "channelFlags":
		case "channelType":
		case "int":
			return new IntegerObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField);
		case "real":
		case "unreal":
			return new FloatObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField);
		case "bool":
			return new BooleanObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField);
		case "unitRace":
			return new GameEnumObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField, "unitRace", "WESTRING_COD_TYPE_UNITRACE",
					StandardObjectData.getUnitEditorData());

		default:
		case "string":
			return new StringObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField);
		}
	}

	protected abstract String getDisplayName(final ObjectData metaData, final War3ID metaKey, final int level,
			MutableGameObject gameObject);

	protected abstract String getDisplayPrefix(ObjectData metaData, War3ID metaKey, int level,
			MutableGameObject gameObject);

	private String getRawDataName(final ObjectData metaData, final War3ID metaKey, final int level) {
		final GameObject metaDataFieldObject = metaData.get(metaKey.toString());
		return MutableObjectData.getEditorMetaDataDisplayKey(level, metaDataFieldObject);
	}
}
