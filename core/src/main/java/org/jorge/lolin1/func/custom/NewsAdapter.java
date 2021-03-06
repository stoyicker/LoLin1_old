package org.jorge.lolin1.func.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.feeds.news.NewsEntry;
import org.jorge.lolin1.io.db.SQLiteDAO;

import java.util.ArrayList;
import java.util.Collections;
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
 * Created by Jorge Antonio Diaz-Benito Soriano on 03/01/14.
 */
public class NewsAdapter extends BaseAdapter {

    private static final int list_item_layout = R.layout.list_item_news_feed;
    private static final HashMap<String, ArrayList<NewsEntry>> shownNews =
            new HashMap<>();
    private static Context mContext;

    public NewsAdapter(Context context) {
        mContext = context;
    }

    public void updateShownNews() {
        String tableName = SQLiteDAO.getNewsTableName();
        //If this table has ever been shown, just update it. Otherwise, add all the new elements.
        if (shownNews.containsKey(tableName)) {
            ArrayList<NewsEntry> currTable = shownNews.get(tableName);
            currTable.clear();
            ArrayList<NewsEntry> newNews = SQLiteDAO.getSingleton().getNews();
            Collections.reverse(newNews);
            for (NewsEntry x : newNews) {
                currTable.add(0, x);
            }
        } else {
            if (SQLiteDAO.tableExists(tableName)) {
                ArrayList<NewsEntry> news =
                        SQLiteDAO.getSingleton().getNews();
                shownNews.put(tableName, news);
            }
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NewsAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        String tableName = SQLiteDAO.getNewsTableName();

        if (shownNews.isEmpty()) {
            return 0;
        }

        ArrayList<NewsEntry> currTable = shownNews.get(tableName);

        if (currTable != null) {
            return currTable.size();
        } else {
            return 0;
        }
    }

    @Override
    public NewsEntry getItem(int i) {
        String tableName = SQLiteDAO.getNewsTableName();
        ArrayList<NewsEntry> currTable = shownNews.get(tableName);

        return currTable.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView =
                    ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(
                                    list_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.setTitleView((TextView) convertView.findViewById(R.id.news_feed_item_title));
            viewHolder.setDescriptionView(
                    (TextView) convertView.findViewById(R.id.news_feed_item_desc));
            viewHolder.setImageView((ImageView) convertView.findViewById(R.id.feed_item_image));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String tableName = SQLiteDAO.getNewsTableName();
        ArrayList<NewsEntry> currentFeed = shownNews.get(tableName);
        NewsEntry thisArticle = currentFeed.get(position);

        viewHolder.getTitleView().setText(Html.fromHtml(thisArticle.getTitle()));
        viewHolder.getDescriptionView().setText(Html.fromHtml(thisArticle.getDescription()));

        convertView.setBackgroundColor(Color.TRANSPARENT);

        new AsyncTask<Object, Void, Void>() {

            @Override
            protected Void doInBackground(final Object... params) {
                final Bitmap bmp = ((NewsEntry) params[1]).getImage(mContext);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ImageView) params[0]).setImageBitmap(bmp);
                    }
                });
                return null;
            }
        }.execute(viewHolder.getImageView(), thisArticle);

        return convertView;
    }

    private final static class ViewHolder {
        private ImageView imageView;
        private TextView titleView, descriptionView;

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public TextView getTitleView() {
            return titleView;
        }

        public void setTitleView(TextView titleView) {
            this.titleView = titleView;
        }

        public TextView getDescriptionView() {
            return descriptionView;
        }

        public void setDescriptionView(TextView descriptionView) {
            this.descriptionView = descriptionView;
        }
    }
}
