package org.jorge.lolin1.utils;

import com.crashlytics.android.Crashlytics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 * Created by JorgeAntonio on 25/01/14.
 */
public final class ISO8601Time {

    private final DateFormat PATTERN = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private Date value;

    public ISO8601Time(String source) {
        try {
            value = PATTERN.parse(source);
        }
        catch (ParseException e) {
            Crashlytics.logException(e);
        }
    }

    public Boolean isMoreRecentThan(String comparisonTarget) {
        Date target = null;
        try {
            target = PATTERN.parse(comparisonTarget);
        }
        catch (ParseException e) {
            Crashlytics.logException(e);
        }

        return value.after(target);
    }
}
