package com.dsatab.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;

public class MetaTalent extends Talent implements JSONable {

	private static final String FIELD_META_TYPE = "metaType";
	private static final String FIELD_FAVORITE = "favorite";
	private static final String FIELD_UNUSED = "unused";

	private static final String DEPRECATED_WACHE_NAME = "Wache";
	private static final String DEPRECATED_KRÄUTERSUCHE_NAME1 = "Kräutersuchen";
	private static final String DEPRECATED_KRÄUTERSUCHE_NAME2 = "Kräuter Suchen";
	private static final String DEPRECATED_PIRSCH_ANSITZ_JAGD = "PirschAnsitzJagd ";

	private boolean favorite, unused;

	public MetaTalent(Hero hero, TalentType type) {
		super(hero);

		this.type = type;
		init();
	}

	public MetaTalent(Hero hero, JSONObject json) throws JSONException {
		super(hero);
		String type = json.getString(FIELD_META_TYPE);
		if (DEPRECATED_KRÄUTERSUCHE_NAME1.equalsIgnoreCase(type)
				|| DEPRECATED_KRÄUTERSUCHE_NAME2.equalsIgnoreCase(type)) {
			this.type = TalentType.Kräutersuchen;
		} else if (DEPRECATED_WACHE_NAME.equalsIgnoreCase(type)) {
			this.type = TalentType.WacheHalten;
		} else if (DEPRECATED_PIRSCH_ANSITZ_JAGD.equalsIgnoreCase(type)) {
			this.type = TalentType.PirschUndAnsitzjagd;
		} else {
			this.type = TalentType.valueOf(type);
		}
		this.favorite = json.getBoolean(FIELD_FAVORITE);
		this.unused = json.getBoolean(FIELD_UNUSED);
		init();
	}

	protected Hero getHero() {
		return (Hero) being;
	}

	/**
	 * 
	 */
	private void init() {
		switch (type) {
		case PirschUndAnsitzjagd:
			probeInfo = ProbeInfo.parse("(MU/IN/GE)");
			break;
		case Kräutersuchen:
		case NahrungSammeln:
			probeInfo = ProbeInfo.parse("(MU/IN/FF)");
			break;
		case WacheHalten:
			probeInfo = ProbeInfo.parse("(MU/IN/KO)");
			break;
		default:
			probeInfo = new ProbeInfo();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Talent#getValue()
	 */
	@Override
	public Integer getValue() {
		switch (type) {
		case PirschUndAnsitzjagd: {
			Integer wildnis = getTalentValue(TalentType.Wildnisleben);
			Integer fährtensuche = getTalentValue(TalentType.Fährtensuchen);
			Integer schleichen = getTalentValue(TalentType.Schleichen);
			Integer tierkunde = getTalentValue(TalentType.Tierkunde);
			Integer distance = 0;

			EquippedItem huntingWeapon = getHero().getHuntingWeapon();
			if (huntingWeapon != null) {
				if (huntingWeapon.getTalent() instanceof CombatDistanceTalent) {
					CombatDistanceTalent distanceTalent = (CombatDistanceTalent) huntingWeapon.getTalent();
					// we want only the acutal talent value without the FK base
					distance = distanceTalent.getValue() - distanceTalent.getBaseValue();
				}
			}

			// Debug.verbose("Wild " + wildnis + " färten " + fährtensuche +
			// " schleich " + schleichen + " tierk " + tierkunde + " fernk " +
			// distance);

			Integer minValue = DsaUtil.min(wildnis, fährtensuche, schleichen, tierkunde, distance);

			// Debug.verbose("Minium value is " + minValue);
			int value = Math.round(DsaUtil.sum(wildnis, fährtensuche, schleichen, tierkunde, distance) / 5.0f);

			// Debug.verbose("Sum value/5 is " + value);
			if (minValue == null)
				return null;
			else {
				value = Math.min(minValue * 2, value);
			}
			return value;
		}
		case Kräutersuchen: {
			Integer wildnis = getTalentValue(TalentType.Wildnisleben);
			Integer sinnen = getTalentValue(TalentType.Sinnenschärfe);
			Integer planzen = getTalentValue(TalentType.Pflanzenkunde);

			Integer minValue = DsaUtil.min(wildnis, sinnen, planzen);
			int value = Math.round(DsaUtil.sum(wildnis, sinnen, planzen) / 3.0f);

			if (minValue == null)
				return null;
			else {
				value = Math.min(minValue * 2, value);
			}
			return value;
		}
		case NahrungSammeln: {
			Integer wildnis = getTalentValue(TalentType.Wildnisleben);
			Integer sinnen = getTalentValue(TalentType.Sinnenschärfe);
			Integer planzen = getTalentValue(TalentType.Pflanzenkunde);

			Integer minValue = DsaUtil.min(wildnis, sinnen, planzen);
			int value = Math.round(DsaUtil.sum(wildnis, sinnen, planzen) / 3.0f);

			if (minValue == null)
				return null;
			else {
				value = Math.min(minValue * 2, value);
			}
			return value;
		}
		case WacheHalten:
			Integer selbst = getTalentValue(TalentType.Selbstbeherrschung);
			Integer sinnen = getTalentValue(TalentType.Sinnenschärfe);
			Integer schleichen = getTalentValue(TalentType.Schleichen);
			Integer verstecken = getTalentValue(TalentType.SichVerstecken);
			Integer wildnis = getTalentValue(TalentType.Wildnisleben);

			Debug.verbose("selbst " + selbst + " sinnen " + sinnen + " schleich " + schleichen + " versteck "
					+ verstecken + " wildn " + wildnis);

			Integer minValue = DsaUtil.min(selbst, sinnen, schleichen, verstecken, wildnis);
			int value = Math.round(DsaUtil.sum(selbst, selbst, selbst, sinnen, sinnen, sinnen, sinnen, schleichen,
					verstecken, wildnis) / 10.0f);

			if (minValue == null)
				return null;
			else {
				value = Math.min(minValue * 2, value);
			}
			return value;

		default:
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Talent#getProbeBonus()
	 */
	@Override
	public Integer getProbeBonus() {
		return getValue();
	}

	private int getTalentValue(TalentType talentName) {
		Talent talent = getHero().getTalent(talentName);

		if (talent == null)
			return 0;
		else
			return talent.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Talent#getProbeType()
	 */
	@Override
	public ProbeType getProbeType() {
		return ProbeType.ThreeOfThree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.MarkableElement#setFavorite(boolean)
	 */
	@Override
	public void setFavorite(boolean value) {
		this.favorite = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.MarkableElement#setUnused(boolean)
	 */
	@Override
	public void setUnused(boolean value) {
		this.unused = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.MarkableElement#isFavorite()
	 */
	@Override
	public boolean isFavorite() {
		return favorite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.MarkableElement#isUnused()
	 */
	@Override
	public boolean isUnused() {
		return unused;
	}

	/**
	 * Constructs a json object with the current data
	 * 
	 * @return
	 * @throws JSONException
	 */
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject out = new JSONObject();

		out.put(FIELD_META_TYPE, type.name());
		out.put(FIELD_UNUSED, unused);
		out.put(FIELD_FAVORITE, favorite);

		return out;
	}

}
