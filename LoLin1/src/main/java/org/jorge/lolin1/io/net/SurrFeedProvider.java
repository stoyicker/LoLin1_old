package org.jorge.lolin1.io.net;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.jorge.lolin1.func.feeds.IFeedHandler;
import org.jorge.lolin1.func.feeds.surr.SurrEntry;
import org.jorge.lolin1.func.feeds.surr.SurrFeedHandler;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.utils.ISO8601Time;
import org.jorge.lolin1.utils.LoLin1Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;

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

    private final Context context;
    private final IFeedHandler handler;

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
            if (LoLin1Utils.isInternetReachable(context)) {
                ArrayList<String> retrievedFeed = retrieveFeed();
                ret = handler.onFeedUpdated(retrievedFeed);
            }
            else {
                handler.onNoInternetConnection();
            }
        }
        catch (IOException e) {
            Log.wtf("debug", e.getClass().getName(), e);
            handler.onNoInternetConnection();
        }
        return ret;
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
        String SURRENDERAT20_URL = "http://feeds.feedburner.com/surrenderat20/CqWw?format=xml";
        URL source = new URL(URLDecoder.decode(SURRENDERAT20_URL, "UTF-8").replace(" ", "%20"));
        URLConnection urlConnection = source.openConnection();
        urlConnection.connect();
        in = new BufferedInputStream(urlConnection.getInputStream());

        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, Boolean.FALSE);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, Boolean.TRUE);
            parser.setInput(in, null);
            parser.nextTag();
            items = readFeed(parser);
        }
        catch (XmlPullParserException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }

        ArrayList<String> ret = new ArrayList<>();
        assert items != null;
        for (SurrEntry item : items) {
            assert item != null;
            ret.add(item.toString());
        }

        Collections.reverse(ret);
        return ret;
    }

    private ArrayList<SurrEntry> readFeed(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        final ArrayList<SurrEntry> ret = new ArrayList<>();
        final String separator = SurrEntry.getSEPARATOR();

        parser.require(XmlPullParser.START_TAG, null, "feed");
        String title = null, link, pubDate = null, updated = null, tagName;
        parser.next();
        while (!(parser.getEventType() == XmlPullParser.END_TAG &&
                parser.getName().contentEquals("feed"))) {
            if (parser.next() == XmlPullParser.START_TAG &&
                    parser.getName().contentEquals("entry")) {
                while (!(parser.next() == XmlPullParser.END_TAG &&
                        (parser.getName()).contentEquals("entry"))) {
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                        tagName = parser.getName();
                        switch (tagName) {
                            case "title":
                                parser.next();
                                title = parser.getText();
                                break;
                            case "published":
                                parser.nextToken();
                                pubDate = parser.getText();
                                break;
                            case "updated":
                                parser.nextToken();
                                updated = parser.getText();
                                break;
                            case "link":
                                if (parser.getAttributeValue(null, "rel")
                                        .contentEquals("alternate")) {
                                    link = parser.getAttributeValue(null, "href");
                                    SurrEntry thisOne =
                                            SQLiteDAO.getSingleton().getSurrByLink(link);
                                    Boolean read = Boolean.FALSE;
                                    if (thisOne != null) {
                                        read = !thisOne.hasBeenRead() ||
                                                new ISO8601Time(updated).isMoreRecentThan(pubDate);
                                    }
                                    ret.add(new SurrEntry(
                                            title + separator + link + separator + pubDate +
                                                    separator +
                                                    updated,
                                            read
                                    ));
                                }
                                break;
                        }
                    }
                }
            }
        }

        return ret;

    }


}
