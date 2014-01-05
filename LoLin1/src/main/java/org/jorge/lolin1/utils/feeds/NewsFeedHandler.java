package org.jorge.lolin1.utils.feeds;

import android.content.Context;
import android.widget.Toast;

import org.jorge.lolin1.utils.ReflectedRes;

import java.util.ArrayList;

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
 * Created by JorgeAntonio on 04/01/14.
 */
public class NewsFeedHandler implements FeedHandler {

    Context context;

    public NewsFeedHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onNoInternetConnection() {
        Toast.makeText(context, ReflectedRes.string(context, "error_no_internet", ""), Toast.LENGTH_LONG);
    }

    @Override
    public void onFeedUpdated(ArrayList<String> items, String separator) {

    }
}
