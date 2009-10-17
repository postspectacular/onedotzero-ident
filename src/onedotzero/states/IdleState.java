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

import onedotzero.CameraConfig;
import onedotzero.ODZApp;
import toxi.util.datatypes.TypedProperties;

/**
 * State specific behaviour when the N900 application is in idle mode (i.e. when
 * a user is typing in a message or not using the app at all).
 */
public class IdleState extends AppState {

	@Override
	public void enter(ODZApp app, TypedProperties camConfig) {
		app.getScheduler().enableProcessQueue(false);
		CameraConfig camera = app.getCamera();
		camera.enableModulation(true);
		camera.targetPos.clear();
		camera.maxModAmpX = camConfig.getFloat("cam.modulation.x.amp", 0.1f);
		camera.maxModAmpY = camConfig.getFloat("cam.modulation.y.amp", 0.1f);
		camera.targetZoom = camConfig.getFloat("defaults.state.idle.zoom",
				0.75f);
		camera.targetTiltOrient.set(CameraConfig.CAM_ORIENTATION_IDLE);
		app.setUpdate(true);
	}

}
