package org.jorge.lolin1.func.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by JorgeAntonio on 02/05/2014.
 */
public class LoLin1AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        LoLin1Authenticator authenticator = new LoLin1Authenticator(getApplicationContext());
        return authenticator.getIBinder();
    }
}