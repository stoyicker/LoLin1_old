package org.jorge.lolin1.io.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jorge.lolin1.utils.feeds.FeedHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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

    private URL source;
    private Context context;
    private String separator;
    private FeedHandler handler;

    /**
     * Constructor with default separator "||||" and immediate parsing request
     *
     * @param source {@link java.net.URL} The data source for the feed
     */
    public NewsFeedProvider(Context context, URL source, FeedHandler handler) {
        this(context, source, "||||", Boolean.TRUE, handler);
    }

    public NewsFeedProvider(Context context, URL source, String separator, FeedHandler handler) {
        this(context, source, separator, Boolean.TRUE, handler);
    }

    public NewsFeedProvider(Context context, URL source, String separator, Boolean immPars, FeedHandler handler) {
        this.source = source;
        this.separator = separator;
        this.context = context;
        this.handler = handler;
        if (immPars) {
            updateFeed();
        }
    }

    public void updateFeed() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    handler.onFeedUpdated(retrieveFeed(), separator);
                } catch (IOException e1) {
                    handler.onNoInternetConnection();
                } catch (XMLStreamException e2) {
                    Log.e("ERROR", "Exception", e2);
                }
                return null;
            }
        }.execute();
    }

    private static final String getCharacterData(XMLEvent event,
                                                 final XMLEventReader eventReader) throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }

        return result;
    }

    private ArrayList<String> retrieveFeed() throws IOException, XMLStreamException {
        final ArrayList<FeedEntry> items = new ArrayList<>();
        final XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        InputStream in;
        in = source.openStream();
        final XMLEventReader eventReader = inputFactory
                .createXMLEventReader(in);
        XMLEvent event;
        boolean descIsUseful = Boolean.FALSE;
        String currPubDate = "";
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
                } else if (local.matches("description")) {
                    if (!descIsUseful) {
                        descIsUseful = Boolean.TRUE;
                        continue;
                    }
                    final FeedEntry currFeed = new FeedEntry(
                            getCharacterData(event, eventReader));
                    currFeed.setPubDate(currPubDate);
                    items.add(currFeed);
                } else if (local.matches("pubDate")) {
                    currPubDate = getCharacterData(event, eventReader);
                }
            }
        }

        ArrayList<String> ret = new ArrayList<>();
        for (Iterator<FeedEntry> it = items.iterator(); it.hasNext(); ) {
            ret.add(it.next().toString(separator));
        }

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
