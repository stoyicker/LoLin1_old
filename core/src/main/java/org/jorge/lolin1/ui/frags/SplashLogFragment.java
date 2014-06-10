package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.devspark.robototextview.widget.RobotoTextView;

import org.jorge.lolin1.R;

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
 * Created by JorgeAntonio on 18/03/14.
 */
public class SplashLogFragment extends Fragment {

    private RobotoTextView logTextView;
    private ScrollView logScrollView;

    public void appendToSameLine(final CharSequence text) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logTextView.append(text);
                }
            });
    }

    public void appendToNewLine(final CharSequence text) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appendToSameLine(new StringBuilder("\n").append(text));
                    logScrollView.post(new Runnable() {
                        public void run() {
                            logScrollView.smoothScrollTo(0, logTextView.getBottom());
                        }
                    });
                }
            });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret =
                inflater.inflate(R.layout.fragment_splash_log_text, container, false);

        logTextView = ((RobotoTextView) ret.findViewById(R.id.fragment_splash_log_text_view));
        logScrollView = (ScrollView) logTextView.getParent();
        return ret;
    }
}
