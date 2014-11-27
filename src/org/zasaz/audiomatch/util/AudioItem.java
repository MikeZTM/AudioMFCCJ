package org.zasaz.audiomatch.util;

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
