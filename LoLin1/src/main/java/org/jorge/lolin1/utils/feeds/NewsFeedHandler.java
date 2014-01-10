package org.jorge.lolin1.utils.feeds;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

import org.jorge.lolin1.io.db.NewsToSQLiteBridge;
import org.jorge.lolin1.utils.Utils;

import java.util.ArrayList;
import java.util.StringTokenizer;

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
        Toast.makeText(context, Utils.getString(context, "error_no_internet", ""), Toast.LENGTH_LONG);
    }

    @Override
    public Boolean onFeedUpdated(ArrayList<String> items, String separator) {
        ContentValues row;
        String url, img_url, title, desc;
        boolean areThereNewNews = Boolean.FALSE;
        for (String x : items) {
            StringTokenizer currentItem = new StringTokenizer(x, separator);
            img_url = currentItem.nextToken();
            url = currentItem.nextToken();
            title = currentItem.nextToken();
            desc = currentItem.nextToken();
            row = new ContentValues();
            row.put(NewsToSQLiteBridge.KEY_TITLE, title);
            row.put(NewsToSQLiteBridge.KEY_DESC, desc);
            row.put(NewsToSQLiteBridge.KEY_IMG_URL, img_url);
            row.put(NewsToSQLiteBridge.KEY_URL, url);
            if (NewsToSQLiteBridge.getSingleton().insertNews(row) != -1) {
                areThereNewNews = Boolean.TRUE;
            }
        }
        return areThereNewNews;
    }
}
