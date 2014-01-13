package org.jorge.lolin1.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.db.NewsToSQLiteBridge;
import org.jorge.lolin1.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
 * Created by JorgeAntonio on 03/01/14.
 */
public class NewsFragmentArrayAdapter extends ArrayAdapter {

    private static final int mResource = R.layout.fragment_news_feed;
    private final HashMap<String, ArrayList<HashMap<String, String>>> shownNews = new HashMap<>();

    public NewsFragmentArrayAdapter(Context context) {
        super(context, mResource);
    }

    private static Bitmap getStoredBitmap(Context context, byte[] blob, String callbackURL) {
        Bitmap ret = null;
        if (blob == null || blob.length == 0) {
            if (Utils.isInternetReachable(context)) {
                try {
                    ret = BitmapFactory
                            .decodeStream(new URL(callbackURL).openConnection().getInputStream());
                    NewsToSQLiteBridge.getSingleton().updateNewsBlob(blob, callbackURL);
                }
                catch (IOException e) {
                    Log.e("ERROR", "Exception", e);
                }
            }
        }
        else {
            ret = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        }
        return ret;
    }

    public void updateShownNews() {
        String tableName = Utils.getTableName(getContext());
        if (shownNews.containsKey(tableName)) {
            ArrayList<HashMap<String, String>> currTable = shownNews.get(tableName);
            int howManyIHave = currTable.size();
            ArrayList<HashMap<String, String>> newNews =
                    NewsToSQLiteBridge.getSingleton().getNewNews(howManyIHave);
            for (HashMap<String, String> x : newNews) {
                currTable.add(x);
                notifyDataSetChanged();
            }
        }
        else {
            ArrayList<HashMap<String, String>> news =
                    NewsToSQLiteBridge.getSingleton().getNews();
            shownNews.put(tableName, news);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        String tableName = Utils.getTableName(getContext());
        ArrayList<HashMap<String, String>> currTable = shownNews.get(tableName);

        return currTable.size();
    }

    @Override
    public Object getItem(int i) {
        String tableName = Utils.getTableName(getContext());
        ArrayList<HashMap<String, String>> currTable = shownNews.get(tableName);

        return currTable.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image = (ImageView) convertView.findViewById(R.id.feed_item_image);
        TextView title = (TextView) convertView.findViewById(R.id.news_feed_item_title);
        TextView desc = (TextView) convertView.findViewById(R.id.news_feed_item_desc);

        int title_proportion =
                Integer.parseInt(Utils.getString(getContext(), "feed_item_title_proportion", "-1"));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        ArrayList<HashMap<String, String>> currentFeed = shownNews.get(prefs
                .getString(Utils.getString(getContext(), "pref_title_server", "pref_title_server"),
                        "pref_title_server")
                + "_" +
                prefs.getString(Utils.getString(getContext(), "pref_title_lang", "pref_title_lang"),
                        "pref_title_lang"));
        HashMap<String, String> thisArticle = currentFeed.get(position);

        Bitmap bmp = getStoredBitmap(getContext(), NewsToSQLiteBridge.getSingleton()
                .getNewsBlob(thisArticle.get(NewsToSQLiteBridge.KEY_IMG_URL)),
                thisArticle.get(NewsToSQLiteBridge.KEY_IMG_URL));
        image.setImageBitmap(bmp);
        title.setTextSize(image.getHeight() * title_proportion);
        desc.setTextSize(image.getHeight() * (1 - title_proportion));
        title.setText(
                Utils.getString(getContext(), thisArticle.get(NewsToSQLiteBridge.KEY_TITLE), ""));
        desc.setText(
                Utils.getString(getContext(), thisArticle.get(NewsToSQLiteBridge.KEY_DESC), ""));

        return convertView;
    }
}
