package org.jorge.lolin1.io.local;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
 * Created by JorgeAntonio on 29/03/2014.
 */
public abstract class FileManager {
    /**
     * Writes a {@link java.io.InputStream} object to a file.
     * The {@link java.io.InputStream} is closed after the operation independently of its success.
     *
     * @param inputStream {@link java.io.InputStream} The stream to read data from.
     * @param target      {@link File} The file to write to.
     * @return {@link Boolean} The success of the operation.
     */
    public static Boolean writeInputStreamToFile(InputStream inputStream, File target) {
        OutputStream outputStream = null;
        try {
            if (!target.exists()) {
                if (!target.createNewFile()) {
                    inputStream.close();
                    return Boolean.FALSE;
                }
            }
            outputStream = new FileOutputStream(target);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        catch (IOException ex) {
            Log.wtf("debug", ex.getClass().getName(), ex);
            return Boolean.FALSE;
        }
        finally {
            try {
                inputStream.close();
                outputStream.close();
            }
            catch (IOException ex) {
                Log.wtf("debug", ex.getClass().getName(), ex);
            }
        }
        return Boolean.TRUE;
    }

    public static Boolean recursiveDelete(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                Boolean success = recursiveDelete(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static String readFile(File fileToRead) {
        StringBuilder ret = new StringBuilder();
        String line;

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToRead));
            while ((line = br.readLine()) != null) {
                ret.append(line);
            }
        }
        catch (IOException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }

        return ret.toString();
    }
}
