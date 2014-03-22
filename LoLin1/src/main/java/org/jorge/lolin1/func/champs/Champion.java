package org.jorge.lolin1.func.champs;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by JorgeAntonio on 02/02/14.
 */
public class Champion {

    //    private final int key;
    private final LinkedList<String> tags = new LinkedList<>();
    //    private final String name, title;
    private final HashMap<Character, Ability> abilityHashMap = new HashMap<>();
    private float hp = -1, hpperlevel = -1, movespeed = -1, armor = -1, armorperlevel = -1, mr = -1,
            mrperlevel = -1, range = -1,
            hpregen = -1, hpregenperlevel = -1, attackdamage = -1, attackdamageperlevel = -1,
            attackspeed = -1,
            attackspeedpercperlevel = -1;

    public Champion(JSONObject data) {
        //TODO
    }

    public void setStats(JSONObject stats) {
        //TODO
    }

    public class Ability {
        private final String description, cost, cooldown, range;

        protected Ability(String description, String cost) {
            this(description, cost, null);
        }

        protected Ability(String description, String cost, String cooldown) {
            this(description, cost, cooldown, null);
        }

        protected Ability(String description, String cost, String cooldown, String range) {
            this.description = description;
            this.cost = cost;
            this.cooldown = cooldown;
            this.range = range;
        }
    }
}
