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
package org.jorge.lolin1.func.champs.models.spells;

import org.json.JSONException;

public class ActiveSpell extends PassiveSpell {

    /**
     * Passive spells are not considered by Riot to have a cooldownBurn nor a
     * rangeBurn.
     */
    private final String cooldownBurn, rangeBurn, costBurn;

    protected ActiveSpell(String name, String detail, String imageName, String cooldownBurn,
                          String rangeBurn, String costBurn) throws JSONException {
        super(name, detail, imageName);
        this.cooldownBurn = cooldownBurn;
        this.rangeBurn = rangeBurn;
        this.costBurn = costBurn;
    }

    protected final String getRangeBurn() {
        return rangeBurn;
    }

    protected final String getCooldownBurn() {
        return cooldownBurn;
    }

    protected final String getCostBurn() {
        return costBurn;
    }
}
