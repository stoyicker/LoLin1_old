package org.jorge.lolin1.io.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

import org.jorge.lolin1.utils.Utils;
import org.jorge.lolin1.utils.feeds.FeedHandler;
import org.jorge.lolin1.utils.feeds.news.FeedEntry;
import org.jorge.lolin1.utils.feeds.news.NewsFeedHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Created by JorgeAntonio on 03/01/14.
 */
public class NewsFeedProvider {

    private static final String AVAILABILITY_CHECKER = "<pubDate>", LOLNEWS_PREFIX =
            "http://feed43.com/lolnews", LOLNEWS_SUFFIX = ".xml";
    private static final Integer AVAILABILITY_DELAY_MILLIS = new Integer(1000);
    private Context context;
    private FeedHandler handler;

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
            if (Utils.isInternetReachable(context)) {
                ArrayList<String> retrievedFeed = retrieveFeed();
                Log.d("NX4", "retrievedFeed size: " + retrievedFeed.size());
                ret = handler.onFeedUpdated(retrievedFeed);
            }
            else {
                handler.onNoInternetConnection();
            }
        }
        catch (IOException e) {
            Log.wtf("NX4", "Should never happen", e);
            handler.onNoInternetConnection();
        }
        finally {
            return ret;
        }
    }

    /**
     * Returns the last page of news.
     *
     * @return {@link java.util.ArrayList} The last page of news. The most recent article is returned last
     * @throws IOException
     */
    private ArrayList<String> retrieveFeed() throws IOException {
        Log.d("NX4", "Coming into retrieveFeed");
        ArrayList<FeedEntry> items = null;
        BufferedInputStream in;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String server = preferences
                .getString(Utils.getString(context, "pref_title_server", "pref_title_server"),
                        "euw"), lang = preferences
                .getString(Utils.getString(context, "pref_title_lang", "pref_title_lang"), "en");

        String langSimplified = Utils.getStringArray(context, "langs_simplified",
                new String[]{"en"})[new ArrayList<>(
                Arrays.asList(Utils.getStringArray(context, "langs", new String[]{"english"})))
                .indexOf(lang)];
        String srcString =
                (LOLNEWS_PREFIX + "_" + server + "_" + langSimplified + LOLNEWS_SUFFIX)
                        .toLowerCase();
        URL source = new URL(srcString);
        URLConnection urlConnection = source.openConnection();
        urlConnection.connect();
        in = new BufferedInputStream(urlConnection.getInputStream());
        //Maybe fix the availability checker?
//        if (!Utils.convertStreamToString(in)
//                .contains(AVAILABILITY_CHECKER)) {
//            try {
//                Thread.sleep(AVAILABILITY_DELAY_MILLIS);
//            }
//            catch (InterruptedException e) {
//                Log.e("ERROR", "Exception", e);
//            }
//            return retrieveFeed();
//        }

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, Boolean.FALSE);
            parser.setInput(in, null);
            parser.nextTag();
            items = readFeed(parser);
        }
        catch (XmlPullParserException e) {
            Log.wtf("NX4", "Should never happen!", e);
        }

        ArrayList<String> ret = new ArrayList<>();
        for (Iterator<FeedEntry> it = items.iterator(); it.hasNext(); ) {
            ret.add(it.next().toString());
        }

        Log.d("NX4", "About to reverse");

        Collections.reverse(ret);
        return ret;
    }

    private ArrayList<FeedEntry> readFeed(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        final ArrayList<FeedEntry> ret = new ArrayList<>();
        Boolean channelDescIsRead = Boolean.FALSE;

        parser.require(XmlPullParser.START_TAG, null, "rss");

        while (!(parser.next() == XmlPullParser.END_TAG && parser.getName().contentEquals("rss"))) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("description")) {
                if (channelDescIsRead) {
                    ret.add(buildFeedEntry(parser));
                }
                else {
                    channelDescIsRead = Boolean.TRUE;
                }
            }
        }

        return ret;
    }

    private FeedEntry buildFeedEntry(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        FeedEntry ret = null;
        parser.require(XmlPullParser.START_TAG, null, "description");
        String name = parser.getName();
        switch (name) {
            case "description":
                ret = new FeedEntry(readText(parser));
                break;
//                case "pubDate": TODO 2 Add pubDate as a field
//                    break;
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
