/*******************************************************************************
 * Copyright (c) 2014 Bert De Geyter (https://github.com/TheHolyWaffle).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Bert De Geyter (https://github.com/TheHolyWaffle)
 ******************************************************************************/
package com.github.theholywaffle.lolchatapi.wrapper;

import android.util.Log;

import com.github.theholywaffle.lolchatapi.ChatMode;
import com.github.theholywaffle.lolchatapi.LoLChat;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;

import org.jdom2.JDOMException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import java.io.IOException;
import java.util.Locale;

/**
 * This and all the files in the module have been developed by Bert De Geyter (https://github.com/TheHolyWaffle) and are protected by the Apache GPLv3 license.
 */
public class Friend extends Wrapper<RosterEntry> implements Comparable {

    private Friend instance = null;
    private Chat chat = null;
    private ChatListener listener = null;

    public Friend(LoLChat api, XMPPConnection connection, RosterEntry entry) {
        super(api, connection, entry);
        this.instance = this;
    }

    /**
     * Deletes this friend.
     *
     * @return true if succesful, otherwise false
     */
    public boolean delete() {
        try {
            try {
                con.getRoster().removeEntry(get());
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | SmackException.NoResponseException e) {
                Log.wtf("debug", e);
//                Crashlytics.logException(e);
            }
            return true;
        } catch (XMPPException e) {
            Log.wtf("debug", e);
//            Crashlytics.logException(e);
        }
        return false;
    }

    private Chat getChat() {
        if (chat == null) {
            chat = ChatManager.getInstanceFor(con).createChat(getUserId(),
                    new MessageListener() {

                        @Override
                        public void processMessage(Chat c, Message m) {
                            if (chat != null && listener != null) {
                                listener.onMessage(instance, m.getBody());
                            }

                        }
                    }
            );
        }
        return chat;
    }

    /**
     * Returns the current ChatMode of this friend (e.g. away, busy,
     * available)
     *
     * @return ChatMode of this friend
     * @see ChatMode
     */
    public ChatMode getChatMode() {
        Presence.Mode mode = con.getRoster().getPresence(getUserId()).getMode();
        for (ChatMode c : ChatMode.values()) {
            if (c.mode == mode) {
                return c;
            }
        }
        return null;
    }

    /**
     * @return the FriendGroup that currently contains this Friend
     */
    public FriendGroup getGroup() {
        return new FriendGroup(api, con, get().getGroups().iterator().next());
    }

    /**
     * @return name of your Friend (e.g. Dyrus)
     */
    public String getName() {
        return get().getName();
    }

    /**
     * Returns Status object that contains all data when hovering over a friend
     * inside League of Legends client (e.g. amount of normal wins, current
     * division and league, queue name, gamestatus,...)
     *
     * @return Status
     */
    public LolStatus getStatus() {
        String status = con.getRoster().getPresence(getUserId()).getStatus();
        if (status != null && !status.isEmpty()) {
            try {
                return new LolStatus(status);
            } catch (JDOMException | IOException e) {
                Log.wtf("debug", e);
//                Crashlytics.logException(e);
            }
        }
        return new LolStatus();
    }

    /**
     * @return the XMPPAddress of your Friend (e.g. sum123456@pvp.net)
     */
    public String getUserId() {
        return get().getUser();
    }

    /**
     * Returns true if this friend is online.
     *
     * @return true if online, false if offline
     */
    public boolean isOnline() {
        return con.getRoster().getPresence(getUserId()).getType() == Type.available;
    }

    /**
     * Sends a message to this friend
     */
    public void sendMessage(String message) {
        try {
            getChat().sendMessage(message);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.wtf("debug", e);
//            Crashlytics.logException(e);
        }
    }

    /**
     * Sends a message to this friend and sets the ChatListener. Only 1
     * ChatListener can be active for each Friend. Any previously set
     * ChatListener will be replaced by this one.
     * <p/>
     * This ChatListener gets called when this Friend only sends you a message.
     *
     * @param message
     * @param listener Your new active ChatListener
     */
    public void sendMessage(String message, ChatListener listener) {
        this.listener = listener;
        try {
            getChat().sendMessage(message);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.wtf("debug", e);
//            Crashlytics.logException(e);
        }
    }

    /**
     * Sets the ChatListener for this friend only. Only 1 ChatListener can be
     * active for each Friend. Any previously set ChatListener for this friend
     * will be replaced by this one.
     * <p/>
     * This ChatListener gets called when this Friend only sends you a message.
     */
    public void setChatListener(ChatListener listener) {
        this.listener = listener;
        getChat();
    }

    public boolean matchesFilterQuery(CharSequence constraint) {
        return getName().toLowerCase(Locale.ENGLISH).contains((constraint + "").toLowerCase(Locale.ENGLISH));
    }

    @Override
    public int compareTo(Object another) {
        if (another == null)
            return -1;
        int thisValue, anotherValue;
        ChatMode thisCM = getChatMode(), anotherCM = ((Friend) another).getChatMode();

        if (thisCM == null)
            thisValue = 0;
        else if (thisCM == ChatMode.AWAY)
            thisValue = 1;
        else if (thisCM == ChatMode.BUSY)
            thisValue = 2;
        else thisValue = 3;
        if (anotherCM == null)
            anotherValue = 0;
        else if (anotherCM == ChatMode.AWAY)
            anotherValue = 1;
        else if (anotherCM == ChatMode.BUSY)
            anotherValue = 2;
        else anotherValue = 3;

        return thisValue == anotherValue ? getName().compareTo(((Friend) another).getName()) : anotherValue - thisValue;
    }
}
