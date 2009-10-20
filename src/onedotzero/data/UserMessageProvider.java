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

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import toxi.math.MathUtils;

/**
 * Implementation of the {@link MessageProvider} interface, pooling messages
 * received from the N900. The class keeps a history of recent messages and
 * stores them as XML files in the "log" folder at a given interval.
 */
@XmlRootElement(name = "messages")
public class UserMessageProvider implements MessageProvider {

    private static final String NAME = "N900 messages";

    protected static final Logger logger =
            Logger.getLogger(UserMessageProvider.class.getName());

    @XmlElement(name = "message")
    public ArrayList<UserMessageLog> messages = new ArrayList<UserMessageLog>();

    @XmlTransient
    private int maxMessageCount;

    public UserMessageProvider() {
        this(10);
    }

    public UserMessageProvider(int maxMessageCount) {
        this.maxMessageCount = maxMessageCount;
    }

    public void addMessage(String msg) {
        if (messages.size() == maxMessageCount) {
            saveMessages();
            messages.clear();
        }
        messages.add(new UserMessageLog(msg));
        logger.info("message added: " + msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.data.MessageProvider#getMessage()
     */
    @Override
    public String getMessage() {
        String msg = null;
        if (messages.size() > 0) {
            msg = messages.get(MathUtils.random(messages.size())).message;
        } else {
            msg = "@onedotzero you guys rock!";
        }
        return msg;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see onedotzero.data.MessageProvider#init()
     */
    @Override
    public boolean init() {
        return true;
    }

    /**
     * Saves recent messages as XML file in the log folder with timestamp.
     */
    private void saveMessages() {
        String path =
                "log/messages-" + (System.currentTimeMillis() / 1000) + ".xml";
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(UserMessageProvider.class);
            context.createMarshaller().marshal(this, new File(path));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}