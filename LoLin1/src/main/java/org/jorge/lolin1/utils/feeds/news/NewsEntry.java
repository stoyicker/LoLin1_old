package org.jorge.lolin1.utils.feeds.news;

import android.content.Context;
import android.graphics.Bitmap;

import org.jorge.lolin1.io.db.NewsToSQLiteBridge;
import org.jorge.lolin1.utils.Utils;

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
 * Created by JorgeAntonio on 20/01/14.
 */
public class NewsEntry {

    private static final String FIELD_SEPARATOR = "||||";
    private final String imageLink, link, title, description;
    private Bitmap image;

    public NewsEntry(final String rawData) {
        final String cleanData = rawData.replaceAll("<p>(.*)", "");
        final StringTokenizer tokenizer = new StringTokenizer(cleanData, "||||");
        this.imageLink = tokenizer.nextToken();
        this.link = tokenizer.nextToken();
        this.title = tokenizer.nextToken();
        this.description = tokenizer.nextToken();
    }

    public static final String getSEPARATOR() {
        return FIELD_SEPARATOR;
    }

    public String toString() {
        return this.imageLink + FIELD_SEPARATOR + this.link + FIELD_SEPARATOR + this.title
                + FIELD_SEPARATOR + this.description + FIELD_SEPARATOR;
    }

//    public Bitmap getImage(Context context) {
//        return Utils.getArticleBitmap(context, NewsToSQLiteBridge.getSingleton()
//                .getArticleBlob(link), imageLink);
//    }

    public Bitmap getImage(Context context) {
        if (this.image == null) {
            this.image = Utils.getArticleBitmap(context, NewsToSQLiteBridge.getSingleton()
                    .getArticleBlob(link), imageLink);
        }
        return this.image;
    }

    public final String getLink() {
        return link;
    }

    public final String getTitle() {
        return title;
    }

    public final String getDescription() {
        return description;
    }
}
