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

package onedotzero.message;

/**
 * This interface is to define listeners of the {@link MessageScheduler}.
 */
public interface MessageScheduleListener {

	/**
	 * Event notification that the entire message queue has been processed.
	 */
	public void messageQueueProcessed();

	/**
	 * Event notification that a new message has been scheduled.
	 * 
	 * @param msg
	 */
	public void messageScheduled(UserMessage msg);
}
