/*
 * This file is part of onedotzero 2009 identity generator (ODZGen).
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import onedotzero.ODZApp;
import onedotzero.poles.ParticlePole3D;
import toxi.color.TColor;
import toxi.math.MathUtils;

/**
 * This class acts as a facade and pool for all message feeds. Used by
 * {@link ODZApp} to retrieve messages for ribbons and to create new
 * {@link ParticlePole3D} constellations of user submitted message for ribbons
 * to flow through.
 */
public class FeedPool {

	/**
	 * Container type for a single feed configuration, incl. color & enable
	 * state.
	 */
	public class FeedConfiguration {
		public final String id;
		public boolean isEnabled;
		public final MessageProvider feed;
		public TColor color, colOrig;

		public FeedConfiguration(String id, MessageProvider feed, TColor col) {
			this.id = id;
			this.feed = feed;
			this.color = col.copy();
			this.colOrig = col.copy();
			isEnabled = true;
			logger.config("new feedconfig: " + id + " col: " + color
					+ " feed: " + feed);
		}

		public void adjustSaturation(float amount) {
			color = colOrig.getSaturated(amount);
		}
	}

	protected static final Logger logger = Logger.getLogger(FeedPool.class
			.getName());

	protected HashMap<String, FeedConfiguration> feedMap = new HashMap<String, FeedConfiguration>();

	protected ArrayList<FeedConfiguration> feedList = new ArrayList<FeedConfiguration>();

	public FeedPool() {

	}

	/**
	 * Initializes a new {@link AtomMessageProvider} and adds it to the pool.
	 * 
	 * @param id
	 *            feed id
	 * @param url
	 *            feed url (needs to be a URL also for local files)
	 * @param col
	 *            feed color
	 * @return feed wrapper object
	 */
	public FeedConfiguration addAtomFeed(String id, String url, TColor col) {
		MessageProvider feed = new AtomMessageProvider(url);
		return addFeed(id, col, feed);
	}

	/**
	 * Initializes the given {@link MessageProvider} instance and if successful
	 * adds it to the pool.
	 * 
	 * @param id
	 *            feed id
	 * @param col
	 *            feed color
	 * @param feed
	 *            message provider
	 * @return feed wrapper object
	 */
	public FeedConfiguration addFeed(String id, TColor col, MessageProvider feed) {
		FeedConfiguration feedConf = null;
		if (feed.init()) {
			feedConf = new FeedConfiguration(id, feed, col);
			feedMap.put(id, feedConf);
			feedList.add(feedConf);
		}
		return feedConf;
	}

	/**
	 * Initializes a new {@link RSSMessageProvider} and adds it to the pool.
	 * 
	 * @param id
	 *            feed id
	 * @param url
	 *            feed url (needs to be a URL also for local files)
	 * @param col
	 *            feed color
	 * @return feed wrapper object
	 */
	public FeedConfiguration addRSSFeed(String id, String url, TColor col) {
		MessageProvider feed = new RSSMessageProvider(url);
		return addFeed(id, col, feed);
	}

	/**
	 * Adjusts the color saturation of all registered feeds by the given amount
	 * (saturation is a normalized value between 0.0..1.0).
	 * 
	 * @param amount
	 *            relative (de)saturation amount
	 */
	public void adjustFeedColors(float amount) {
		for (FeedConfiguration f : feedList) {
			f.adjustSaturation(amount);
		}
	}

	/**
	 * Returns a list of all registered feeds.
	 * 
	 * @return the feedList
	 */
	public ArrayList<FeedConfiguration> getFeedList() {
		return feedList;
	}

	/**
	 * Returns a message from a randomly picked registed feed.
	 * 
	 * @return
	 */
	public String getMessage() {
		return getRandomActiveFeed().feed.getMessage();
	}

	/**
	 * Returns a randomly chosen active feed (i.e. its state is not disabled).
	 * 
	 * @return
	 */
	public FeedConfiguration getRandomActiveFeed() {
		FeedConfiguration f = null;
		while (f == null || !f.isEnabled) {
			f = feedList.get(MathUtils.random(feedList.size()));
		}
		return f;
	}

	/**
	 * Enables/disables the feed at the given index in the feed pool (used by a
	 * GUI callback for feed check boxes).
	 * 
	 * @param id
	 *            feed index
	 */
	public void toggleFeedStatus(int id) {
		FeedConfiguration feed = feedList.get(id);
		feed.isEnabled = !feed.isEnabled;
	}
}
