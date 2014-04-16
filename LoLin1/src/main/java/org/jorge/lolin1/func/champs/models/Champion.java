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

import android.util.Log;

import org.jorge.lolin1.func.champs.models.spells.AbstractSpellFactory;
import org.jorge.lolin1.func.champs.models.spells.ActiveSpell;
import org.jorge.lolin1.func.champs.models.spells.PassiveSpell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class Champion {

    private String key, name, title, attackrange, mpperlevel, mp, attackdamage,
            hp, hpperlevel, attackdamageperlevel, armor, mpregenperlevel,
            hpregen, critperlevel, spellblockperlevel, mpregen,
            attackspeedperlevel, spellblock, movespeed, attackspeedoffset,
            crit, hpregenperlevel, armorperlevel, lore, imageName;
    private final String[] tags;
    private final ActiveSpell[] spells;
    private final PassiveSpell passive;
    private String[] skins;

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

    public Boolean containsText(CharSequence text) {
        try {
            Field[] fields = Champion.class.getDeclaredFields();
            for (Field x : fields) {
                Class type = x.getType();
                x.setAccessible(Boolean.TRUE);
                if (!type.isArray() && type == String.class) {
                    if (x.get(this).toString().contains(text)) {
                        x.setAccessible(Boolean.FALSE);
                        return Boolean.TRUE;
                    }
                }
                else if (type.isArray() && x.getType() == String.class) {
                    String[] thisStringArray = (String[]) x.get(this);
                    for (String y : thisStringArray) {
                        if (y.contains(text)) {
                            x.setAccessible(Boolean.FALSE);
                            return Boolean.TRUE;
                        }
                    }
                }
                else if (type == PassiveSpell.class) {
                    PassiveSpell thisPassiveSpell = (PassiveSpell) x.get(this);
                    Field[] passiveSpellFields = PassiveSpell.class.getDeclaredFields();
                    for (Field y : passiveSpellFields) {
                        y.setAccessible(Boolean.TRUE);
                        if (y.get(thisPassiveSpell).toString().contains(text)) {
                            y.setAccessible(Boolean.FALSE);
                            x.setAccessible(Boolean.FALSE);
                            return Boolean.TRUE;
                        }
                        y.setAccessible(Boolean.FALSE);
                    }
                }
                else if (type.isArray() && x.getType() == ActiveSpell.class) {
                    ActiveSpell[] thisActiveSpellArray = (ActiveSpell[]) x.get(this);
                    for (ActiveSpell eachActiveSpell : thisActiveSpellArray) {
                        Field[] eachActiveSpellFieldArray = ActiveSpell.class.getDeclaredFields();
                        for (Field y : eachActiveSpellFieldArray) {
                            if (y.get(eachActiveSpell).toString().contains(text)) {
                                y.setAccessible(Boolean.FALSE);
                                x.setAccessible(Boolean.FALSE);
                                return Boolean.TRUE;
                            }
                            y.setAccessible(Boolean.FALSE);
                        }
                    }
                }
                x.setAccessible(Boolean.FALSE);
            }
        }
        catch (IllegalAccessException ex) {
            Log.wtf("debug", ex.getClass().getName(), ex);
        }
        return Boolean.FALSE;
    }
}
