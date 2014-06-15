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
package com.github.theholywaffle.lolchatapi;

/**
 * This and all the files in the module have been developed by Bert De Geyter (https://github.com/TheHolyWaffle) and are protected by the Apache GPLv3 license.
 */
public enum ChatServer {

    //Chat suggestions taken from http://leagueoflegends.wikia.com/wiki/Talk:XMPP_Chat

    EUW("chat.eu.lol.riotgames.com"), //Confirmed (216.133.234.21, 216.133.234.22)
    NA("chat.na1.lol.riotgames.com"), //Confirmed through lookup
    EUNE("chat.eun1.lol.riotgames.com"), //Confirmed through lookup
    TR("chat.tr.lol.riotgames.com"),//Confirmed through lookup
    RU("chat.ru.lol.riotgames.com"),//Confirmed through lookup
    BR("chat.br.lol.riotgames.com"),//Confirmed through lookup
    LAN("chat.la1.lol.riotgames.com"),//Confirmed through lookup
    KR("chat.kr.lol.riotgames.com"),//Confirmed through lookup
    OCE("chat.oc1.lol.riotgames.com"),//Confirmed through lookup
    LAS("chat.la2.lol.riotgames.com");//Confirmed through lookup
//    TW("chattw.lol.garenanow.com"),
//    TH("chatth.lol.garenanow.com"),
//    PH("chatph.lol.garenanow.com"),
//    VN("chatvn.lol.garenanow.com");

    String host;

    ChatServer(String host) {
        this.host = host;
    }

}
