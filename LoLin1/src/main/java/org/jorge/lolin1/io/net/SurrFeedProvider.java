package org.jorge.lolin1.io.net;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.jorge.lolin1.feeds.IFeedHandler;
import org.jorge.lolin1.feeds.surr.SurrEntry;
import org.jorge.lolin1.feeds.surr.SurrFeedHandler;
import org.jorge.lolin1.io.db.SQLiteBridge;
import org.jorge.lolin1.utils.ISO8601Time;
import org.jorge.lolin1.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
public class SurrFeedProvider {

    private static final String SURRENDERAT20_URL =
            "http://feeds.feedburner.com/surrenderat20/CqWw?format=xml";
    private Context context;
    private IFeedHandler handler;

    /**
     * Constructor with default separator "||||"
     *
     * @param context {@link android.content.Context} The application context
     */
    public SurrFeedProvider(Context context) {
        this.context = context;
        this.handler = new SurrFeedHandler(context);
    }

    public Boolean requestFeedRefresh() {
        Boolean ret = Boolean.FALSE;
        try {
            if (Utils.isInternetReachable(context)) {
                ArrayList<String> retrievedFeed = retrieveFeed();
                ret = handler.onFeedUpdated(retrievedFeed);
            }
            else {
                handler.onNoInternetConnection();
            }
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            Log.wtf("ERROR", "Should never happen", e);
            handler.onNoInternetConnection();
        }
        finally {
            return ret;
        }
    }

    /**
     * Returns the lastest three surrs.
     *
     * @return {@link java.util.ArrayList} The lastest three surrs. The most recent article is returned last
     * @throws java.io.IOException
     */
    private ArrayList<String> retrieveFeed() throws IOException {
        ArrayList<SurrEntry> items = null;
        BufferedInputStream in;
        URL source = new URL(SURRENDERAT20_URL);
        URLConnection urlConnection = source.openConnection();
        urlConnection.connect();
        in = new BufferedInputStream(urlConnection.getInputStream());

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, Boolean.FALSE);
            parser.setInput(in, null);
            parser.nextTag();
            items = readFeed(parser);
        }
        catch (XmlPullParserException e) {
            Log.wtf("ERROR", "XML discarded", e);
        }

        ArrayList<String> ret = new ArrayList<>();
        for (Iterator<SurrEntry> it = items.iterator(); it.hasNext(); ) {
            ret.add(it.next().toString());
        }

        Collections.reverse(ret);
        return ret;
    }

    private ArrayList<SurrEntry> readFeed(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        final ArrayList<SurrEntry> ret = new ArrayList<>();
        final String separator = SurrEntry.getSEPARATOR();

        parser.require(XmlPullParser.START_TAG, null, "feed");
        String title = null, link = null, pubDate = null, updated = null, tagName = null;
        parser.next();
        while (!(parser.getEventType() == XmlPullParser.END_TAG &&
                parser.getName().contentEquals("feed"))) {
            if (!parser.getName().contentEquals("entry")) {
                parser.nextTag();
            }
            else {
                while (!(parser.next() == XmlPullParser.END_TAG &&
                        (tagName = parser.getName()).contentEquals("entry"))) {
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                        switch (tagName) {
                            case "title":
                                title = parser.getText();
                                break;
                            case "published":
                                pubDate = parser.getText();
                                break;
                            case "updated":
                                updated = parser.getText();
                                break;
                            case "link":
                                if (parser.getAttributeValue(null, "rel")
                                        .contentEquals("alternate")) {
                                    link = parser.getAttributeValue(null, "href");
                                }
                                break;
                        }
                    }
                }
                ret.add(new SurrEntry(title + separator + link + pubDate + updated,
                        !SQLiteBridge.getSingleton().getSurrByTitle(title).hasBeenRead() ||
                                new ISO8601Time(updated)
                                        .isMoreRecentThan(pubDate)));
            }
        }

        return ret;
    }
}
