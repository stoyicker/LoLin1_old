package org.jorge.lolin1.custom;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import org.jorge.lolin1.utils.LoLin1Utils;

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
 * Created by JorgeAntonio on 07/01/14.
 */
public class TimePickerPreference extends DialogPreference
        implements TimePicker.OnTimeChangedListener {

    private static final String SANITY_EXPRESSION = "[0-5]*[0-9]:[0-5]*[0-9]";
    private static final Integer ERROR_CODE = -1;
    private static Integer TYPE_COUNTER = 0;
    private String defaultTime;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(Boolean.TRUE);
        String instanceType;
        switch (TYPE_COUNTER % 3) {
            case 0:
                instanceType = "baron";
                break;
            case 1:
                instanceType = "dragon";
                break;
            case 2:
                instanceType = "buff";
                break;
            default:
                Log.e("TYPE COUNTER", "" + TYPE_COUNTER);
                instanceType = "ERROR";
        }
        defaultTime =
                LoLin1Utils.getString(getContext(), "pref_default_" + instanceType + "_respawn",
                        "ERROR");
        TYPE_COUNTER++;
    }

    @Override
    protected View onCreateDialogView() {
        TimePicker timePicker = new TimePicker(getContext());
        timePicker.setOnTimeChangedListener(this);

        int mm = getMinutes();
        int ss = getSeconds();

        timePicker.setIs24HourView(Boolean.TRUE);

        if (mm != ERROR_CODE && ss != ERROR_CODE) {
            timePicker.setCurrentHour(mm);
            timePicker.setCurrentMinute(ss);
        }

        return timePicker;
    }

    @Override
    public void onTimeChanged(TimePicker view, int minutes, int seconds) {

        String result = minutes + ":" + seconds;
        callChangeListener(result);
        persistString(result);
    }

    /**
     * @return {@link java.lang.Integer} The minutes, which will be 0 to 59 (inclusive)
     */
    private Integer getMinutes() {
        String time = getPersistedString(this.defaultTime);

        if (time == null || !time.matches(SANITY_EXPRESSION)) {
            return ERROR_CODE;
        }

        return Integer.valueOf(time.split(":")[0]);
    }

    /**
     * @return {@link java.lang.Integer} The seconds, which will be from 0 to 59 (inclusive)
     */
    private Integer getSeconds() {
        String time = getPersistedString(this.defaultTime);

        if (time == null || !time.matches(SANITY_EXPRESSION)) {
            return ERROR_CODE;
        }

        return Integer.valueOf(time.split(":")[1]);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue)
        //Restore state
        {
            defaultTime = getPersistedString(defaultTime);
        }
        else {
            //Set state
            defaultTime = (String) defaultValue;
            persistString(defaultTime);
        }
    }
}
