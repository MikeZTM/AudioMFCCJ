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

import comirva.audio.feature.AudioFeature;

/**
 * Created by Mike on 11/27/2014.
 */
public class AudioItem {
    private String filename;
    private AudioFeature gmm;

    public AudioItem(String filename, AudioFeature gmm) {
        this.filename = filename;
        this.gmm = gmm;
    }

    public String getFilename(){
        return this.filename;
    }

    public AudioFeature getGMM(){
        return this.gmm;
    }
}
