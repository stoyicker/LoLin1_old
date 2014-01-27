package org.jorge.lolin1.feeds.surr;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.jorge.lolin1.feeds.IFeedHandler;
import org.jorge.lolin1.io.db.SQLiteBridge;
import org.jorge.lolin1.utils.ISO8601Time;
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
 * Created by JorgeAntonio on 25/01/14.
 */
public class SurrFeedHandler implements IFeedHandler {

    Context context;

    public SurrFeedHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onNoInternetConnection() {
        final String msg = Utils.getString(context, "error_no_internet", "ERROR");
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
        boolean isRefreshRequired = Boolean.FALSE;
        SurrEntry current;
        String title, updated;
        for (String x : items) {
            Log.d("NX4", "SFH: " + x);
            row = new ContentValues();
            StringTokenizer tokenizer = new StringTokenizer(x, SurrEntry.getSEPARATOR());
            row.put(SQLiteBridge.SURR_KEY_TITLE, title = tokenizer.nextToken().replaceAll("comilla",
                    "'"));
            row.put(SQLiteBridge.SURR_KEY_LINK, tokenizer.nextToken());
            current = SQLiteBridge.getSingleton().getSurrByTitle(title);
            row.put(SQLiteBridge.SURR_KEY_PUBLISHED, tokenizer.nextToken());
            row.put(SQLiteBridge.SURR_KEY_UPDATED, updated = tokenizer.nextToken());
            Boolean pendingRead = Boolean.FALSE;
            if (current != null) {
                pendingRead =
                        !current.hasBeenRead() || new ISO8601Time(updated)
                                .isMoreRecentThan(current.getUpdateString()) ?
                                Boolean.TRUE : Boolean.FALSE;
            }
            row.put(SQLiteBridge.SURR_KEY_READ, !pendingRead);
            if (SQLiteBridge.getSingleton().insertSurrArticle(row) != -1) {
                isRefreshRequired = Boolean.TRUE;
            }
            else if (SQLiteBridge.getSingleton().updateSurrArticle(title, row) != 0) {
                isRefreshRequired = Boolean.TRUE;
            }
        }
        return isRefreshRequired;
    }
}
