package org.zasaz.audiomatch.util;

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
