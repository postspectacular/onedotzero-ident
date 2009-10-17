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

package onedotzero.states;

import onedotzero.ODZApp;
import toxi.util.datatypes.TypedProperties;

/**
 * Abstract base class for specifying application states & state specific
 * behaviours of the main {@link ODZApp}.
 */
public abstract class AppState {

	/**
	 * All concrete subclasses need to implement this method called when the app
	 * enters this state.
	 * 
	 * @param app
	 *            main app
	 * @param camConfig
	 *            camera config settings
	 */
	public abstract void enter(ODZApp app, TypedProperties camConfig);

	/**
	 * Called by {@link ODZApp} at the beginning of each render loop iteration.
	 * This provides an oppotunity to manipulate camera settings. Not all states
	 * need to react to this.
	 * 
	 * @param app
	 *            main app
	 */
	public void pre(ODZApp app) {

	}

	/**
	 * Called by {@link ODZApp} just before updating ribbons in each iteration
	 * of the render loop. Not all states need to react to this.
	 * 
	 * @param app
	 *            main app
	 */
	public void update(ODZApp app) {

	}

	/**
	 * Called by {@link ODZApp} when a new tilt vector has been received. Not
	 * all states have to react to this.
	 * 
	 * @param app
	 *            main app
	 * @param x
	 *            tilt x
	 * @param y
	 *            tilt y
	 * @param z
	 *            tilt z
	 */
	public void updateTilt(ODZApp app, float x, float y, float z) {

	}
}
