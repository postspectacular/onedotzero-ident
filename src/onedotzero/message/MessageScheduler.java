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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

/**
 * Implements the queuing and scheduling of user submitted message using a
 * {@link PriorityBlockingQueue}. The scheduler runs in its own thread
 * broadcasting events specified via the {@link MessageScheduleListener}
 * interface at key moments. Submitted messages are kept in a buffer so that
 * they can be re-shown in succession even if no new messages have been
 * submitted meanwhile.
 */
public class MessageScheduler extends Thread {

    protected static final Logger logger =
            Logger.getLogger(MessageScheduler.class.getName());

    private PriorityBlockingQueue<UserMessage> queue =
            new PriorityBlockingQueue<UserMessage>();

    private ArrayList<UserMessage> recentMessages =
            new ArrayList<UserMessage>();

    private UserMessage currentMessage;

    private List<MessageScheduleListener> listeners =
            new ArrayList<MessageScheduleListener>();

    private boolean isActive = true;

    private boolean doProcessQueue = true;

    private int maxRecentMessages;

    public MessageScheduler(int maxRecentMessages) {
        this.maxRecentMessages = maxRecentMessages;
    }

    public void addListener(MessageScheduleListener l) {
        listeners.add(l);
    }

    /**
     * Adds a new message to the priority queue. Newly submitted messages have
     * the highest priority which is automatically reduced each time it's been
     * shown.
     * 
     * @param m
     */
    public void addMessage(UserMessage m) {
        queue.offer(m);
        logger.info("new message added: " + m);
        synchronized (recentMessages) {
            recentMessages.add(m);
            if (recentMessages.size() > maxRecentMessages) {
                recentMessages.remove(0);
                logger.info("removing oldest...");
            }
        }
    }

    public void enableProcessQueue(boolean state) {
        logger.info("processing queue: " + state);
        doProcessQueue = state;
    }

    public ArrayList<UserMessage> getRecentMessages() {
        return recentMessages;
    }

    public void replayRecent() {
        logger.info("replay recent messages...");
        synchronized (recentMessages) {
            for (UserMessage m : recentMessages) {
                m.reducePriority();
                queue.offer(m);
                logger.info("new message added: " + m);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (isActive) {
                if (doProcessQueue) {
                    if (currentMessage == null
                            || (currentMessage != null && !currentMessage
                                    .isActive())) {
                        triggerNextMessage();
                    }
                    UserMessage nxt = queue.peek();
                    if (nxt != null && currentMessage != null
                            && nxt.getPriority() > currentMessage.getPriority()) {
                        triggerNextMessage();
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
        }
    }

    public void setDefaultMessage(UserMessage m) {
        queue.offer(m);
        triggerNextMessage();
    }

    public void shutdown() {
        isActive = false;
    }

    private void triggerNextMessage() {
        UserMessage prev = currentMessage;
        currentMessage = queue.poll();
        if (currentMessage != null) {
            logger.info("new message triggered: " + currentMessage);
            currentMessage.activate();
            for (MessageScheduleListener l : listeners) {
                l.messageScheduled(currentMessage);
            }
        } else if (prev != null) {
            for (MessageScheduleListener l : listeners) {
                l.messageQueueProcessed();
            }
        }
    }
}