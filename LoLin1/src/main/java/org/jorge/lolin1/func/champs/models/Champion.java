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
import android.util.Log;

import org.jorge.lolin1.func.champs.models.spells.AbstractSpellFactory;
import org.jorge.lolin1.func.champs.models.spells.ActiveSpell;
import org.jorge.lolin1.func.champs.models.spells.PassiveSpell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class Champion implements Parcelable {

    private String key, name, title, attackrange, mpperlevel, mp, attackdamage,
            hp, hpperlevel, attackdamageperlevel, armor, mpregenperlevel,
            hpregen, critperlevel, spellblockperlevel, mpregen,
            attackspeedperlevel, spellblock, movespeed, attackspeedoffset,
            crit, hpregenperlevel, armorperlevel, lore, imageName;
    private String[] tags, skins;
    private PassiveSpell passive;
    private ActiveSpell[] spells;

    public Champion(Parcel in) {
        Field[] declaredFields = Champion.class.getDeclaredFields();
        try {
            for (Field x : declaredFields) {
                Class<?> thisType = x.getType();
                if (thisType == String.class) {
                    x.setAccessible(Boolean.TRUE);
                    if (thisType.isArray()) {
                        String[] values = null;
                        in.readStringArray(values);
                        x.set(this, values);
                    }
                    else {
                        x.set(this, in.readString());
                    }
                    x.setAccessible(Boolean.FALSE);
                }
            }
        }
        catch (IllegalAccessException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
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
                }
                catch (IllegalAccessException | JSONException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
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
        for (int i = 0; i < size; i++)
            this.skins[i] = skinsDescriptor.getString(i);
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

    public String getPassiveImageName() {
        return this.passive.getImageName();
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
        String lowerCaseFilterText = filterText.toString().toLowerCase();
        if (this.getName().toLowerCase().contains(lowerCaseFilterText)) {
            return Boolean.TRUE;
        }
        if (this.getSimplifiedName().toLowerCase().contains(lowerCaseFilterText)) {
            return Boolean.TRUE;
        }
        if (this.getTitle().toLowerCase().contains(lowerCaseFilterText)) {
            return Boolean.TRUE;
        }
        String[] tags = this.getTags();
        for (String tag : tags)
            if (tag.toLowerCase().contains(lowerCaseFilterText)) {
                return Boolean.TRUE;
            }
        if (this.getPassive().getName().toLowerCase().contains(lowerCaseFilterText)) {
            return Boolean.TRUE;
        }
        ActiveSpell[] spells = this.getSpells();
        for (ActiveSpell spell : spells) {
            if (spell.getName().toLowerCase().contains(lowerCaseFilterText)) {
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
                    if (thisType.isArray()) {
                        dest.writeStringArray((String[]) x.get(this));
                    }
                    else {
                        dest.writeString(x.get(this).toString());
                    }
                    x.setAccessible(Boolean.FALSE);
                }
            }
        }
        catch (IllegalAccessException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
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
