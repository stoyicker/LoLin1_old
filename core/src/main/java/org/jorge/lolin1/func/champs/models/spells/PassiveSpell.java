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

import android.os.Parcel;
import android.os.Parcelable;

public class PassiveSpell implements Parcelable {

    private String imageName, detail, name;

    protected PassiveSpell(String _name, String _detail, String _imageName) {
        this.name = _name;
        this.detail = _detail;
        this.imageName = _imageName;
    }

    public PassiveSpell(Parcel in) {
        this.name = in.readString();
        this.detail = in.readString();
        this.imageName = in.readString();
    }

    public final String getImageName() {
        return this.imageName;
    }

    public final String getDetail() {
        return detail;
    }

    public final String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getName());
        dest.writeString(this.getDetail());
        dest.writeString(this.getImageName());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PassiveSpell createFromParcel(Parcel in) {
            return new PassiveSpell(in);
        }

        public PassiveSpell[] newArray(int size) {
            return new PassiveSpell[size];
        }
    };
}
