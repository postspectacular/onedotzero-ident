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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import onedotzero.ODZApp;
import toxi.data.feeds.AtomEntry;
import toxi.data.feeds.AtomFeed;
import toxi.data.feeds.util.EntityStripper;
import toxi.util.datatypes.TypedProperties;

/**
 * If SMS messaging is enabled, this class is monitoring and polling the
 * moderated SMS message feed (Atom formatted) and automatically triggers newly
 * received messages.
 */
public class SMSProvider extends Thread {

    protected static final Logger logger =
            Logger.getLogger(SMSProvider.class.getName());

    private TypedProperties config;
    private boolean isActive = true;

    private Set<String> messageIndex;

    private ODZApp app;

    public SMSProvider(ODZApp app, TypedProperties conf) {
        this.app = app;
        this.config = conf;
        this.messageIndex =
                Collections.synchronizedSet(new HashSet<String>(100));
    }

    public boolean refresh() {
        String feedUrl = config.getProperty("sms.feed.url");
        logger.info("reloading sms feed: " + feedUrl);
        try {
            JAXBContext context = JAXBContext.newInstance(AtomFeed.class);
            AtomFeed feed =
                    (AtomFeed) context.createUnmarshaller().unmarshal(
                            new URL(feedUrl));
            logger.info(feed.entries.size() + " entries loaded");
            for (AtomEntry e : feed.entries) {
                if (!messageIndex.contains(e.id)) {
                    app.setMessage(EntityStripper.flattenXML(e.title));
                    messageIndex.add(e.id);
                } else {
                    logger.info("ignoring entry: " + e.id);
                }
            }
            return true;
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        try {
            while (isActive) {
                refresh();
                Thread.sleep(config.getInt("sms.feed.poll.delay", 30000));
            }
        } catch (InterruptedException e) {
            logger.warning("sms thread interrupted");
            isActive = false;
        }
    }

    public void shutdown() {
        isActive = false;
        interrupt();
    }
}
