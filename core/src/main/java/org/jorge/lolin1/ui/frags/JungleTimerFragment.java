package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.R;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 25/04/2014.
 */
public abstract class JungleTimerFragment extends Fragment {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
    private InnerCountDownTimer chronometer;
    private int background_color;
    private Boolean isChronometerRunning = Boolean.FALSE;
    private Date initialValueAsDate;
    private long initialValue, lastTimeTracked;
    private TextView jungleTimeView;
    private PowerManager.WakeLock mWakeLock;

    public JungleTimerFragment() {
        setRetainInstance(Boolean.TRUE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mWakeLock = ((PowerManager) activity.getApplicationContext().getSystemService(
                Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getName());
        background_color = -1;
        String prefName = null;

        if (this instanceof BlueJungleTimerFragment) {
            background_color = R.color.theme_light_blue;
            prefName = "buff";
        } else if (this instanceof RedJungleTimerFragment) {
            background_color = R.color.theme_red;
            prefName = "buff";
        } else if (this instanceof BaronJungleTimerFragment) {
            background_color = R.color.theme_purple;
            prefName = "baron";
        } else if (this instanceof DrakeJungleTimerFragment) {
            background_color = R.color.theme_strong_orange;
            prefName = "dragon";
        }

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(activity.getApplicationContext());

        String key = "pref_title_" + prefName + "_respawn";
        prefName = preferences.getString(key, "00:30");
        try {
            initialValueAsDate = SDF.parse(prefName);
            initialValue =
                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).parse("01-01-1970 01:" + prefName)
                            .getTime();
        } catch (ParseException e) {
            Crashlytics.logException(e);
        }

        chronometer = new InnerCountDownTimer(initialValue, 1000);
    }

    @Override
    public void onDestroyView() {
        if (PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getBoolean("pref_title_display", Boolean.TRUE)) {
            mWakeLock.release();
        }
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_jungle_timer, container, false);

        if (PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getBoolean("pref_title_display", Boolean.TRUE)) {
            mWakeLock.acquire();
        }

        jungleTimeView = (TextView) ret.findViewById(R.id.jungle_time_view);

        jungleTimeView.setBackgroundResource(background_color);

        jungleTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChronometerRunning) {
                    chronometer.start();
                    isChronometerRunning = Boolean.TRUE;
                } else {
                    chronometer.cancel();
                    chronometer = new InnerCountDownTimer(lastTimeTracked, 1000);
                    isChronometerRunning = Boolean.FALSE;
                }
            }
        });

        jungleTimeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                chronometer.cancel();
                chronometer = new InnerCountDownTimer(initialValue, 1000);
                lastTimeTracked = 0;
                isChronometerRunning = Boolean.FALSE;
                jungleTimeView.setText(SDF.format(initialValueAsDate));
                return Boolean.TRUE;
            }
        });
        if (jungleTimeView.getText().toString().isEmpty()) {
            String prefName = null;

            if (this instanceof BlueJungleTimerFragment) {
                background_color = R.color.theme_light_blue;
                prefName = "buff";
            } else if (this instanceof RedJungleTimerFragment) {
                background_color = R.color.theme_red;
                prefName = "buff";
            } else if (this instanceof BaronJungleTimerFragment) {
                background_color = R.color.theme_purple;
                prefName = "baron";
            } else if (this instanceof DrakeJungleTimerFragment) {
                background_color = R.color.theme_strong_orange;
                prefName = "dragon";
            }
            jungleTimeView.setText(PreferenceManager
                    .getDefaultSharedPreferences(getActivity().getApplicationContext())
                    .getString("pref_title_" + prefName + "_respawn", "00:30"));
        }
        return ret;
    }


    private class InnerCountDownTimer extends CountDownTimer {

        private InnerCountDownTimer(long initialValue, long tickInterval) {
            super(initialValue, tickInterval);
        }

        @Override
        public void onTick(final long millisUntilFinished) {
            lastTimeTracked = millisUntilFinished;
            if (jungleTimeView != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        jungleTimeView
                                .setText(SDF
                                        .format(new Date(millisUntilFinished).getTime()));
                    }
                });
            }
        }

        @Override
        public void onFinish() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (jungleTimeView != null) {
                        jungleTimeView.setText(SDF.format(initialValueAsDate));
                    }
                }
            });
            isChronometerRunning = Boolean.FALSE;
        }
    }
}
