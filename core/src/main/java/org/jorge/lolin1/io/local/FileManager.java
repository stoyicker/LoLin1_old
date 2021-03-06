package org.jorge.lolin1.io.local;

import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 29/03/2014.
 */
public abstract class FileManager {

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

    public static void writeStringToFile(String string, File file, String locale)
            throws IOException {

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(string.getBytes(LoLin1Utils.getLocaleCharset(locale)));
        outputStream.close();
    }

    public static String readFileAsString(File target)
            throws IOException {

        char[] buff = new char[1024];
        StringBuilder builder = new StringBuilder();

        FileReader reader = new FileReader(target);

        while (reader.read(buff) != -1) {
            builder.append(buff);
        }

        reader.close();

        return builder.toString();
    }
}
