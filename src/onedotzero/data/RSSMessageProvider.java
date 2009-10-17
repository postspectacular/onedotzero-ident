/*
 * This file is part of onedotzero 2009 identity generator (ODZGen).
 * 
 * Copyright 2009 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * ODZGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ODZGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ODZGen. If not, see <http://www.gnu.org/licenses/>.
 */

package onedotzero.data;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import toxi.data.feeds.RSSFeed;
import toxi.math.MathUtils;

/**
 * Implementation of the {@link MessageProvider} interface and wrapper for an
 * RSS2.0 message feed.
 */
public class RSSMessageProvider implements MessageProvider {

    private RSSFeed feed;
    private String feedUrl;

    public RSSMessageProvider(String url) {
        feedUrl = url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.data.MessageProvider#getMessage()
     */
    @Override
    public String getMessage() {
        return feed.channel.items.get(
                MathUtils.random(feed.channel.items.size())).getTitlePlain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.data.MessageProvider#init()
     */
    @Override
    public boolean init() {
        try {
            JAXBContext context = JAXBContext.newInstance(RSSFeed.class);
            feed =
                    (RSSFeed) context.createUnmarshaller().unmarshal(
                            new URL(feedUrl));
            return true;
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + feedUrl + " items: "
                + feed.channel.items.size();
    }
}
