/**
 * This file is part of lolin1-data-provider.

 lolin1-data-provider is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 lolin1-data-provider is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with lolin1-data-provider.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jorge.lolin1.func.champs.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.func.champs.models.spells.AbstractSpellFactory;
import org.jorge.lolin1.func.champs.models.spells.ActiveSpell;
import org.jorge.lolin1.func.champs.models.spells.PassiveSpell;
import org.jorge.lolin1.io.local.JsonManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Locale;

public final class Champion implements Parcelable {

    private String key, name, title, attackrange, mpperlevel, mp, attackdamage,
            hp, hpperlevel, attackdamageperlevel, armor, mpregenperlevel,
            hpregen, critperlevel, spellblockperlevel, mpregen,
            attackspeedperlevel, spellblock, movespeed, attackspeedoffset,
            crit, hpregenperlevel, armorperlevel, lore, imageName;
    private String[] tags, skins;
    private PassiveSpell passive;
    private ActiveSpell[] spells;

    public enum ChampionResource {
        NONE, MANA, ENERGY
    }

    public String getResource() {
        return mp;
    }

    public String getResourcePerLevel() {
        return mpperlevel;
    }

    public String getResourceRegen() {
        return mpregen;
    }

    public String getResourceRegenPerLevel() {
        return mpregenperlevel;
    }

    public ChampionResource getUsedResource() {
        final String MANA_STRING = "mana", ENERGY_STRING = "energy";
        int manaCounter = 0, energyCounter = 0;
        for (ActiveSpell x : spells) {
            String lowerCaseCost = x.getCostBurn().toLowerCase(Locale.ENGLISH);
            if (lowerCaseCost.contains(MANA_STRING.toLowerCase(Locale.ENGLISH))) {
                manaCounter++;
            }
            if (lowerCaseCost.contains(ENERGY_STRING.toLowerCase(Locale.ENGLISH))) {
                energyCounter++;
            }
        }
        if (manaCounter == 0) {
            if (energyCounter == 0) {
                return ChampionResource.NONE;
            } else {
                return ChampionResource.ENERGY;
            }
        } else {
            return energyCounter > manaCounter ? ChampionResource.ENERGY : ChampionResource.MANA;
        }
    }

    public String getMagicResist() {
        return spellblock;
    }

    public String getMagicResistPerLevel() {
        return spellblockperlevel;
    }

    public String getAttackSpeed() {
        //Source: http://leagueoflegends.wikia.com/wiki/Attack_delay
        double d = (0.625 / (1 + Float.parseFloat(attackspeedoffset)));
        String r = d + "";
        return r.length() > 5 ? r.substring(0, 5) : r;
    }

    public String getAttackSpeedPerLevel() {
        return attackspeedperlevel;
    }

    public String getArmorPerLevel() {
        return armorperlevel;
    }

    public String getArmor() {
        return armor;
    }

    public String getHpRegenPerLevel() {
        return hpregenperlevel;
    }

    public String getMoveSpeed() {
        return movespeed;
    }

    public String getAttackDamagePerLevel() {
        return attackdamageperlevel.length() > 5 ? attackdamageperlevel.substring(0, 5) :
                attackdamageperlevel;
    }

    public String getAttackDamage() {
        return attackdamage.length() > 5 ? attackdamage.substring(0, 5) : attackdamage;
    }

    public String getHpPerlevel() {
        return hpperlevel;
    }

    public String getHp() {
        return hp;
    }

    public String getHpRegen() {
        return hpregen;
    }

    public Champion(Parcel in) {
        Field[] declaredFields = Champion.class.getDeclaredFields();
        try {
            for (Field x : declaredFields) {
                Class<?> thisType = x.getType();
                if (thisType == String.class) {
                    x.setAccessible(Boolean.TRUE);
                    x.set(this, in.readString());
                    x.setAccessible(Boolean.FALSE);
                }
            }
        } catch (IllegalAccessException e) {
            Crashlytics.logException(e);
        }
        tags = in.createStringArray();
        skins = in.createStringArray();
        passive = in.readParcelable(PassiveSpell.class.getClassLoader());
        Parcelable[] parcelableSpells = in.readParcelableArray(ActiveSpell.class.getClassLoader());
        spells = new ActiveSpell[parcelableSpells.length];
        for (int i = 0; i < spells.length; i++) {
            spells[i] = (ActiveSpell) parcelableSpells[i];
        }
    }

    public Champion(JSONObject descriptor) throws JSONException {
        Field[] fields = Champion.class.getDeclaredFields();
        for (Field x : fields) {
            x.setAccessible(Boolean.TRUE);
            if (x.getType() == String.class) {
                try {
                    x.set(this, descriptor.getString(x.getName()));
                } catch (IllegalAccessException | JSONException e) {
                    Crashlytics.logException(e);
                }
            }
            x.setAccessible(Boolean.FALSE);
        }
        passive = AbstractSpellFactory.createPassiveSpell(descriptor.getJSONObject("passive"));
        JSONArray activeSpellsDescriptor = descriptor.getJSONArray("spells");
        int size = activeSpellsDescriptor.length();
        spells = new ActiveSpell[size];
        for (int i = 0; i < size; i++)
            spells[i] =
                    AbstractSpellFactory.createActiveSpell(activeSpellsDescriptor.getJSONObject(i));
        JSONArray tagsDescriptor = descriptor.getJSONArray("tags");
        size = tagsDescriptor.length();
        this.tags = new String[size];
        for (int i = 0; i < size; i++)
            this.tags[i] = tagsDescriptor.getString(i);
        JSONArray skinsDescriptor = descriptor.getJSONArray("skins");
        size = skinsDescriptor.length();
        this.skins = new String[size];
        for (int i = 0; i < size; i++) {
            this.skins[i] = JsonManager.getStringAttribute(skinsDescriptor.getString(i), "name");
        }
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String[] getTags() {
        return tags;
    }

    public String[] getSkinNames() {
        return skins;
    }

    public String getBustImageName() {
        return this.imageName;
    }

    public String[] getSpellImageNames() {
        String[] ret = new String[this.spells.length];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.spells[i].getImageName();
        }

        return ret;
    }

    public String getSimplifiedName() {
        String imageName = getBustImageName();
        return imageName.substring(0, imageName.indexOf("."));
    }

    public Boolean matchesFilterQuery(CharSequence filterText) {
        String lowerCaseFilterText = filterText.toString().toLowerCase(Locale.ENGLISH);
        if (this.getName().toLowerCase(Locale.ENGLISH).contains(lowerCaseFilterText)) {
            return Boolean.TRUE;
        }
        if (this.getSimplifiedName().toLowerCase(Locale.ENGLISH).contains(lowerCaseFilterText)) {
            return Boolean.TRUE;
        }
        if (this.getTitle().toLowerCase(Locale.ENGLISH).contains(lowerCaseFilterText)) {
            return Boolean.TRUE;
        }
        String[] tags = this.getTags();
        for (String tag : tags)
            if (tag.toLowerCase(Locale.ENGLISH).contains(lowerCaseFilterText)) {
                return Boolean.TRUE;
            }
        if (this.getPassive().getName().toLowerCase(Locale.ENGLISH).contains(lowerCaseFilterText)) {
            return Boolean.TRUE;
        }
        ActiveSpell[] spells = this.getSpells();
        for (ActiveSpell spell : spells) {
            if (spell.getName().toLowerCase(Locale.ENGLISH).contains(lowerCaseFilterText)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public PassiveSpell getPassive() {
        return passive;
    }

    public ActiveSpell[] getSpells() {
        return spells;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Field[] declaredFields = Champion.class.getDeclaredFields();
        try {
            for (Field x : declaredFields) {
                Class<?> thisType = x.getType();
                if (thisType == String.class) {
                    x.setAccessible(Boolean.TRUE);
                    dest.writeString(x.get(this).toString());
                    x.setAccessible(Boolean.FALSE);
                }
            }
        } catch (IllegalAccessException e) {
            Crashlytics.logException(e);
        }
        dest.writeStringArray(tags);
        dest.writeStringArray(skins);
        dest.writeParcelable(this.passive, flags);
        dest.writeParcelableArray(this.spells, flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Champion createFromParcel(Parcel in) {
            return new Champion(in);
        }

        public Champion[] newArray(int size) {
            return new Champion[size];
        }
    };

    public String getLore() {
        return lore;
    }
}
