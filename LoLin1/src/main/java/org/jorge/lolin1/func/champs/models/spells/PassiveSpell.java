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

public class PassiveSpell {

    private final String imageName;
    private final String detail;
    private final String name;

    protected PassiveSpell(String _name, String _detail, String _imageName) {
        this.name = _name;
        this.detail = _detail;
        this.imageName = _imageName;
    }

    public final String getImageName() {
        return this.imageName;
    }

    protected final String getDetail() {
        return detail;
    }

    public final String getName() {
        return name;
    }
}
