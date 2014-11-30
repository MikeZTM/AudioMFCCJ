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
    public static ArrayList<AudioItem> items=new ArrayList<>();

    public static void addToLibrary(File file) throws IOException, UnsupportedAudioFileException {
        AudioFeature audioFeature = null;
        TimbreDistributionExtractor featureExtractor = new TimbreDistributionExtractor();
        AudioInputStream in;
        in = AudioSystem.getAudioInputStream(file);
        audioFeature = (AudioFeature) featureExtractor.calculate(in);
        in.close();
        AudioItem item = new AudioItem(file.getName(), audioFeature);
        AudioLibrary.items.add(item);
    }

    public static String tryToMatch(File file) throws IOException, UnsupportedAudioFileException {
        AudioFeature audioFeature = null;
        TimbreDistributionExtractor featureExtractor = new TimbreDistributionExtractor();
        AudioInputStream in;
        in = AudioSystem.getAudioInputStream(file);
        audioFeature = (AudioFeature) featureExtractor.calculate(in);
        in.close();
        double nearest = 10000;
        String name = "Not Matched.";
        for (AudioItem i : AudioLibrary.items) {
            double dis = audioFeature.getDistance(i.getGMM());
            if (dis < nearest) {
                nearest = dis;
                name = i.getFilename();
            }
        }
        return name;
    }
}
