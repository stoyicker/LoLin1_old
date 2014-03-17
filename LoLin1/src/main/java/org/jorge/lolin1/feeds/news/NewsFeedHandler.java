package org.jorge.lolin1.feeds.news;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import org.jorge.lolin1.feeds.IFeedHandler;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
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
public class NewsFeedHandler implements IFeedHandler {

    Context context;

    public NewsFeedHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onNoInternetConnection() {
        final String msg = LoLin1Utils.getString(context, "error_no_internet", "ERROR");
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Boolean onFeedUpdated(ArrayList<String> items) {
        ContentValues row;
        String url, img_url, title, desc;
        boolean areThereNewNews = Boolean.FALSE;
        for (String x : items) {
            StringTokenizer currentItem = new StringTokenizer(x, NewsEntry.getFieldSeparator());
            img_url = currentItem.nextToken();
            url = currentItem.nextToken();
            title = currentItem.nextToken();
            desc = currentItem.nextToken();
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory
                        .decodeStream(new URL(img_url).openConnection()
                                .getInputStream());
            }
            catch (IOException e) {
                Log.w("ERROR", "Should never happen!", e);
            }

            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 0, blob);
            byte[] bmpAsByteArray = blob.toByteArray();
            row = new ContentValues();
            row.put(SQLiteDAO.NEWS_KEY_TITLE, title);
            row.put(SQLiteDAO.NEWS_KEY_DESC, desc);
            row.put(SQLiteDAO.NEWS_KEY_IMG_URL, img_url.replaceAll("http://", "httpxxx"));
            row.put(SQLiteDAO.NEWS_KEY_URL, url.replaceAll("http://", "httpxxx"));
            row.put(SQLiteDAO.NEWS_KEY_BLOB, bmpAsByteArray);
            if (SQLiteDAO.getSingleton().insertNewsArticle(row) != -1) {
                areThereNewNews = Boolean.TRUE;
            }
        }
        return areThereNewNews;
    }
}
