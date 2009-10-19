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

package onedotzero.message;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Container type for a user submitted message. Implements the
 * {@link Comparable} interface used by the {@link PriorityBlockingQueue} of the
 * {@link MessageScheduler}. Each message has a priority, birth time and
 * time-to-live setting. A message's priority is automatically reduced each time
 * it's been shown.
 */
public class UserMessage implements Comparable<UserMessage> {

    private List<MessageLine> lines;
    private long birthTime;
    private int ttl;
    private int priority;
    private int hash;

    public UserMessage(List<MessageLine> lines, int ttl, int priority) {
        this.lines = lines;
        this.ttl = ttl;
        this.priority = priority;
        for (MessageLine l : lines) {
            this.hash = 37 * this.hash + l.hashCode();
        }
    }

    public void activate() {
        birthTime = System.currentTimeMillis();
    }

    /**
     * Compares this priority of this message with the other one. Used by the
     * {@link PriorityBlockingQueue} in {@link MessageScheduler}.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(UserMessage m) {
        return m.priority - priority;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserMessage) {
            return priority == ((UserMessage) obj).priority;
        }
        return false;
    }

    public List<MessageLine> getContent() {
        return lines;
    }

    public int getPriority() {
        return priority;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return hash;
    }

    public boolean isActive() {
        return System.currentTimeMillis() - birthTime < ttl;
    }

    public void reducePriority() {
        if (priority > 0) {
            priority--;
        }
    }

    public void setPriority(int p) {
        priority = p;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(64);
        for (MessageLine l : lines) {
            result.append(l.text).append(" ");
        }
        return result.toString();
    }
}
