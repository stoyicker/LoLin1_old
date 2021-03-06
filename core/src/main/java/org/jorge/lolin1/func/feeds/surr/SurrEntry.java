package org.jorge.lolin1.func.feeds.surr;

import org.jorge.lolin1.func.feeds.BaseEntry;
import org.jorge.lolin1.io.db.SQLiteDAO;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 20/01/14.
 */
public class SurrEntry extends BaseEntry {

    private static final String FIELD_SEPARATOR = "||||";
    private final String pubDate, updated;
    private boolean read;

    public SurrEntry(final String data, Boolean read) {
        final StringTokenizer tokenizer = new StringTokenizer(data, FIELD_SEPARATOR);
        this.title = tokenizer.nextToken();
        this.link = tokenizer.nextToken().replaceAll("httpxxx", "http://");
        this.pubDate = tokenizer.nextToken();
        this.updated = tokenizer.nextToken();
        this.read = read;
    }

    public static String getSEPARATOR() {
        return FIELD_SEPARATOR;
    }

    public final Boolean hasBeenRead() {
        return this.read;
    }

    public final String getUpdateString() {
        return this.updated;
    }

    public void markAsRead() {
        this.read = Boolean.TRUE;
        SQLiteDAO.getSingleton().markSurrAsRead(this.getLink());
    }

    public String toString() {

        return getTitle() + getSEPARATOR() + getLink() + getSEPARATOR() + pubDate + getSEPARATOR() +
                getUpdateString();
    }
}
