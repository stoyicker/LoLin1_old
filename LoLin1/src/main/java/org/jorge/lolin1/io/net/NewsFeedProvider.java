package org.jorge.lolin1.io.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jorge.lolin1.utils.Utils;
import org.jorge.lolin1.utils.feeds.FeedHandler;
import org.jorge.lolin1.utils.feeds.NewsFeedHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;

import mf.javax.xml.stream.XMLEventReader;
import mf.javax.xml.stream.XMLInputFactory;
import mf.javax.xml.stream.XMLStreamException;
import mf.javax.xml.stream.events.Characters;
import mf.javax.xml.stream.events.XMLEvent;

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

    private static final String AVAILABILITY_CHECKER = "stylesheet", LOLNEWS_PREFIX =
            "http://feed43.com/lolnews", LOLNEWS_SUFFIX = ".xml";
    private static final Integer AVAILABILITY_DELAY_MILLIS = new Integer(1000);
    private Context context;
    private String separator;
    private FeedHandler handler;

    /**
     * Constructor with default separator "||||"
     *
     * @param context {@link Context} The application context
     */
    public NewsFeedProvider(Context context) {
        this.context = context;
        this.separator = "||||";
        this.handler = new NewsFeedHandler(context);
    }

    private static final String getCharacterData(XMLEvent event,
                                                 final XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }

        return result;
    }

    public Boolean requestFeedRefresh() {
        Boolean ret = Boolean.FALSE;
        try {
            if (Utils.isInternetReachable(context)) {
                ret = handler.onFeedUpdated(retrieveFeed(), separator);
            }
            else {
                handler.onNoInternetConnection();
            }
        }
        catch (IOException e1) {
            handler.onNoInternetConnection();
        }
        catch (XMLStreamException e2) {
            Log.e("ERROR", "Exception", e2);
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
     * @throws XMLStreamException
     */
    private ArrayList<String> retrieveFeed() throws IOException, XMLStreamException {
        final ArrayList<FeedEntry> items = new ArrayList<>();
        final XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        InputStream in;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String server = preferences
                .getString(Utils.getString(context, "pref_title_server", "pref_title_server"),
                        "euw"), lang = preferences
                .getString(Utils.getString(context, "pref_title_lang", "pref_title_lang"), "en");
        String srcString =
                (LOLNEWS_PREFIX + "_" + server + "_" + lang + LOLNEWS_SUFFIX).toLowerCase();
        URL source = new URL(srcString);
        in = source.openStream();
        if (!Utils.convertStreamToString(in).contains(AVAILABILITY_CHECKER)) {
            try {
                Thread.sleep(AVAILABILITY_DELAY_MILLIS);
            }
            catch (InterruptedException e) {
                Log.e("ERROR", "Exception", e);
            }
            return retrieveFeed();
        }
        final XMLEventReader eventReader = inputFactory
                .createXMLEventReader(in);
        XMLEvent event;
        boolean descIsUseful = Boolean.FALSE;
        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();
            if (event.isStartElement()) {
                final String local = event.asStartElement().getName()
                        .getLocalPart();
                if (local.matches("rss") || local.matches("channel")
                        || local.matches("title") || local.matches("link")
                        || local.matches("lastBuildDate")
                        || local.matches("generator") || local.matches("ttl")) {
                    continue;
                }
                else if (local.matches("description")) {
                    if (!descIsUseful) {
                        descIsUseful = Boolean.TRUE;
                        continue;
                    }
                    final FeedEntry currFeed = new FeedEntry(
                            getCharacterData(event, eventReader));
                    items.add(currFeed);
                }
                else if (local.matches("pubDate")) {
                    getCharacterData(event,
                            eventReader); //FUTURE This field tells when the article was published.
                }
            }
        }

        ArrayList<String> ret = new ArrayList<>();
        for (Iterator<FeedEntry> it = items.iterator(); it.hasNext(); ) {
            ret.add(it.next().toString(separator));
        }
        Collections.reverse(ret);
        return ret;
    }

    private class FeedEntry {

        private final String imageLink, titleLink, title, subtitle;

        private String pubDate;

        protected FeedEntry(final String data) {
            final String cleanData = data.replaceAll("<p>(.*)", "");
            final StringTokenizer tokenizer = new StringTokenizer(cleanData, "||||");
            this.imageLink = tokenizer.nextToken();
            this.titleLink = tokenizer.nextToken();
            this.title = tokenizer.nextToken();
            this.subtitle = tokenizer.nextToken();
        }

        protected final void setPubDate(final String _pubDate) {
            this.pubDate = _pubDate;
        }

        public String toString(String separator) {
            return this.imageLink + separator + this.titleLink + separator + this.title
                    + separator + this.subtitle + separator + this.pubDate;
        }
    }

}
