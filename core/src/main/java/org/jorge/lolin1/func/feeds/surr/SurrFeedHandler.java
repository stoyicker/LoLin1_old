package org.jorge.lolin1.func.feeds.surr;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

import org.jorge.lolin1.func.feeds.IFeedHandler;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.utils.ISO8601Time;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.util.Collection;
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
 * Created by Jorge Antonio Diaz-Benito Soriano on 25/01/14.
 */
public class SurrFeedHandler implements IFeedHandler {

    private final Context context;

    public SurrFeedHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onNoInternetConnection() {
        final String msg = LoLin1Utils.getString(context, "error_no_connection", null);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Boolean onFeedUpdated(Collection<String> items) {
        ContentValues row;
        boolean isRefreshRequired = Boolean.FALSE;
        SurrEntry current;
        String link, updated;
        for (String x : items) {
            row = new ContentValues();
            StringTokenizer tokenizer = new StringTokenizer(x, SurrEntry.getSEPARATOR());
            row.put(SQLiteDAO.SURR_KEY_TITLE, tokenizer.nextToken());
            row.put(SQLiteDAO.SURR_KEY_LINK, link = tokenizer.nextToken().replaceAll("http://",
                    "httpxxx"));
            current = SQLiteDAO.getSingleton().getSurrByLink(link);
            row.put(SQLiteDAO.SURR_KEY_PUBLISHED, tokenizer.nextToken());
            row.put(SQLiteDAO.SURR_KEY_UPDATED, updated = tokenizer.nextToken());
            Boolean pendingRead = Boolean.TRUE;
            if (current != null) {
                pendingRead =
                        !current.hasBeenRead() || new ISO8601Time(updated)
                                .isMoreRecentThan(current.getUpdateString()) ?
                                Boolean.TRUE : Boolean.FALSE;
            }
            row.put(SQLiteDAO.SURR_KEY_READ, !pendingRead);
            if (SQLiteDAO.getSingleton().insertSurrArticle(row) != -1) {
                isRefreshRequired = Boolean.TRUE;
            } else if (SQLiteDAO.getSingleton().updateSurrArticleByLink(link, row) != 0) {
                isRefreshRequired = Boolean.TRUE;
            }
        }
        return isRefreshRequired;
    }
}
