package org.jorge.lolin1.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.db.NewsToSQLiteBridge;
import org.jorge.lolin1.utils.Utils;
import org.jorge.lolin1.utils.feeds.news.FeedEntry;

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
public class NewsFragmentArrayAdapter extends BaseAdapter {

    private static final int list_item_layout = R.layout.list_item_news_feed;
    private static final HashMap<String, ArrayList<FeedEntry>> shownNews =
            new HashMap<>();
    private static Context mContext;

    public NewsFragmentArrayAdapter(Context context) {
        mContext = context;
    }

    public void updateShownNews() {
        String tableName = Utils.getTableName(mContext);
        Log.d("NX4", "Table name fetched: " + tableName);
        //If this table has ever been shown it, just update it. Otherwise, add all the new elements.
        if (shownNews.containsKey(tableName)) {
            Log.d("NX4", "updateShownNews is on the if.");
            ArrayList<FeedEntry> currTable = shownNews.get(tableName);
            int howManyIHave = currTable.size();
            ArrayList<FeedEntry> newNews =
                    NewsToSQLiteBridge.getSingleton().getNewNews(howManyIHave);
            for (FeedEntry x : newNews) {
                currTable.add(x);
            }
        }
        else {
            Log.d("NX4", "updateShownNews is on the else.");
            if (Utils.tableExists(tableName)) {
                ArrayList<FeedEntry> news =
                        NewsToSQLiteBridge.getSingleton().getNews();
                Log.d("NX4", "news size is " + news.size());
                shownNews.put(tableName, news);
            }
        }
        Log.d("NX4", "About to call notifyDataSetChanged");
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        String tableName = Utils.getTableName(mContext);
        ArrayList<FeedEntry> currTable = shownNews.get(tableName);

        Log.d("NX4", "Calling getCount");
        return currTable.size();
    }

    @Override
    public FeedEntry getItem(int i) {
        String tableName = Utils.getTableName(mContext);
        ArrayList<FeedEntry> currTable = shownNews.get(tableName);

        return currTable.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =
                    ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(
                                    list_item_layout, null);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.feed_item_image);
        TextView title = (TextView) convertView.findViewById(R.id.news_feed_item_title);
        TextView desc = (TextView) convertView.findViewById(R.id.news_feed_item_desc);

        String tableName = Utils.getTableName(mContext);
        ArrayList<FeedEntry> currentFeed = shownNews.get(tableName);
        FeedEntry thisArticle = currentFeed.get(position);

        title.setText(Html.fromHtml(thisArticle.getTitle()));
        desc.setText(Html.fromHtml(thisArticle.getDescription()));

        new AsyncTask<Object, Void, Void>() {
            /**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             * <p/>
             * This method can call {@link #publishProgress} to publish updates
             * on the UI thread.
             *
             * @param params The parameters of the task.
             * @return A result, defined by the subclass of this task.
             * @see #onPreExecute()
             * @see #onPostExecute
             * @see #publishProgress
             */
            @Override
            protected Void doInBackground(final Object... params) {
                final Bitmap bmp = ((FeedEntry) params[1]).getImage(mContext);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ImageView) params[0]).setImageBitmap(bmp);
                    }
                });
                return null;
            }
        }.execute(image, thisArticle);

        return convertView;
    }
}
