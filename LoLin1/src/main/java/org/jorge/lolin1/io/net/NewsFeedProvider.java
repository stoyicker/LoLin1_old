package org.jorge.lolin1.io.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

import org.jorge.lolin1.func.feeds.IFeedHandler;
import org.jorge.lolin1.func.feeds.news.NewsEntry;
import org.jorge.lolin1.func.feeds.news.NewsFeedHandler;
import org.jorge.lolin1.utils.LoLin1Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Created by JorgeAntonio on 03/01/14.
 */
public class NewsFeedProvider {

    private final Context context;
    private final IFeedHandler handler;

    /**
     * Constructor with default separator "||||"
     *
     * @param context {@link Context} The application context
     */
    public NewsFeedProvider(Context context) {
        this.context = context;
        this.handler = new NewsFeedHandler(context);
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
            e.printStackTrace(System.err);
            Log.wtf("ERROR", "Should never happen", e);
            handler.onNoInternetConnection();
        }
        return ret;
    }

    /**
     * Returns the last page of news.
     *
     * @return {@link java.util.ArrayList} The last page of news. The most recent article is returned last
     * @throws IOException
     */
    private ArrayList<String> retrieveFeed() throws IOException {
        ArrayList<NewsEntry> items = null;
        BufferedInputStream in;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String server = preferences
                .getString(LoLin1Utils.getString(context, "pref_title_server", "pref_title_server"),
                        "euw"), lang = preferences
                .getString(LoLin1Utils.getString(context, "pref_title_lang", "pref_title_lang"),
                        "en");

        String langSimplified = LoLin1Utils.getStringArray(context, "langs_simplified",
                new String[]{"en"})[new ArrayList<>(
                Arrays.asList(
                        LoLin1Utils.getStringArray(context, "langs", new String[]{"english"}))
        )
                .indexOf(lang)], LOLNEWS_PREFIX = "http://feed43.com/lolnews", LOLNEWS_SUFFIX =
                ".xml";
        String srcString =
                (LOLNEWS_PREFIX + "_" + server + "_" + langSimplified + LOLNEWS_SUFFIX)
                        .toLowerCase();
        URL source = new URL(srcString);
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
        for (NewsEntry item : items) {
            ret.add(item.toString());
        }

        Collections.reverse(ret);
        return ret;
    }

    private ArrayList<NewsEntry> readFeed(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        final ArrayList<NewsEntry> ret = new ArrayList<>();
        Boolean channelDescIsRead = Boolean.FALSE;

        parser.require(XmlPullParser.START_TAG, null, "rss");

        while (!(parser.next() == XmlPullParser.END_TAG && parser.getName().contentEquals("rss"))) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("description")) {
                if (channelDescIsRead) {
                    ret.add(buildNewsEntry(parser));
                }
                else {
                    channelDescIsRead = Boolean.TRUE;
                }
            }
        }

        return ret;
    }

    private NewsEntry buildNewsEntry(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        NewsEntry ret = null;
        parser.require(XmlPullParser.START_TAG, null, "description");
        String name = parser.getName();
        switch (name) {
            case "description":
                ret = new NewsEntry(readText(parser));
                break;
            default:
                skip(parser);
        }
        return ret;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String ret = "";

        if (parser.next() == XmlPullParser.TEXT) {
            ret = parser.getText();
        }

        return ret;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        String name = parser.getName();
        if (name.contentEquals("guid") || name.contentEquals("pubDate")) {
            parser.nextTag();
            return;
        }
        while (parser.next() != XmlPullParser.END_DOCUMENT && (parser.getName() == null ||
                !parser.getName().contentEquals("description"))) {
        }
    }
}
