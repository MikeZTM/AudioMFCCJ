package org.zasaz.audiomatch.util;

//Copyright (C) 2014  Yingzhuo Li
//
//        This program is free software: you can redistribute it and/or modify
//        it under the terms of the GNU General Public License as published by
//        the Free Software Foundation, either version 3 of the License, or
//        (at your option) any later version.
//
//        This program is distributed in the hope that it will be useful,
//        but WITHOUT ANY WARRANTY; without even the implied warranty of
//        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//        GNU General Public License for more details.
//
//        You should have received a copy of the GNU General Public License
//        along with this program.  If not, see <http://www.gnu.org/licenses/>.

import java.io.File;
import javax.swing.filechooser.*;

/**
 * Created by Mike on 11/27/2014.
 */
public class MP3Filter extends FileFilter {

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = "";

        int i = f.getName().lastIndexOf('.');
        if (i >= 0) {
            extension = f.getName().substring(i+1);
        }
        if (extension != null) {
            if (extension.toLowerCase().equals("mp3")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "MP3 Files";
    }

}
