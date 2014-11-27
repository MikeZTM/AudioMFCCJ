package org.zasaz.audiomatch.util;

import comirva.audio.extraction.TimbreDistributionExtractor;
import comirva.audio.feature.AudioFeature;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mike on 11/27/2014.
 */
public class AudioLibrary {
    public static ArrayList<AudioItem> items;

    public static void addToLibrary(File file) throws IOException, UnsupportedAudioFileException {
        AudioFeature audioFeature = null;
        TimbreDistributionExtractor featureExtractor = new TimbreDistributionExtractor();
        AudioInputStream in;
        in = AudioSystem.getAudioInputStream(file);
        audioFeature = (AudioFeature) featureExtractor.calculate(in);
        in.close();
        AudioItem item=new AudioItem(file.getName(),audioFeature);
        AudioLibrary.items.add(item);
    }
}
