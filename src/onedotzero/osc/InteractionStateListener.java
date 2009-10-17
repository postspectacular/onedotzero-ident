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

package onedotzero.osc;

import onedotzero.ODZApp;
import onedotzero.states.AppState;
import oscP5.OscMessage;

/**
 * This interface defines event notifications triggered by the
 * {@link OSCManager}. The interface is implemented by {@link ODZApp}.
 */
public interface InteractionStateListener {

	/**
	 * Notification that an OSC message not directly handled by the
	 * {@link OSCManager} has been received.
	 * 
	 * @param msg
	 *            received message
	 */
	void oscMessageReceived(OscMessage msg);

	void setAppState(AppState state);

	/**
	 * Sets a new message received from the N900.
	 * 
	 * @param message
	 */
	void setMessage(String message);

	/**
	 * Updates the shake vector and strength.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param strength
	 */
	void setShake(float x, float y, float z, float strength);

	/**
	 * Updates the tilt vector.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	void setTilt(float x, float y, float z);

	/**
	 * Updates the touch position.
	 * 
	 * @param x
	 * @param y
	 */
	void setTouch(float x, float y);

	/**
	 * Updates the zoom level.
	 * 
	 * @param zoom
	 */
	void setZoom(float zoom);

	/**
	 * Toggles the play/pause button (used to freeze ribbon animation).
	 */
	void togglePlayButton();
}
