package org.zasaz.audiomatch;

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

import comirva.audio.extraction.MFCCExtractor;
import comirva.audio.extraction.TimbreDistributionExtractor;
import comirva.audio.feature.AudioFeature;
import comirva.audio.util.PointList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class Main {

    public static void main(String[] args) {

        MainUI main=new MainUI();
//        try {
//            File is = new File("C:\\Users\\Mike\\Indie Rocker_alt1.mp3");
//            AudioFeature audioFeature_o = null;
//            TimbreDistributionExtractor featureExtractor = new TimbreDistributionExtractor();
//            AudioInputStream in;
//            in = AudioSystem.getAudioInputStream(is);
//            audioFeature_o = (AudioFeature) featureExtractor.calculate(in);
//            in.close();
//
//            is = new File("C:\\Users\\Mike\\Indie Rocker_full.mp3");
//            AudioFeature audioFeature_new = null;
//            in = AudioSystem.getAudioInputStream(is);
//            audioFeature_new = (AudioFeature) featureExtractor.calculate(in);
//            in.close();
//
//            is = new File("C:\\Users\\Mike\\Indie Rocker_alt1_1.mp3");
//            AudioFeature audioFeature_new2 = null;
//            in = AudioSystem.getAudioInputStream(is);
//            audioFeature_new2 = (AudioFeature) featureExtractor.calculate(in);
//            in.close();
//
//            double dis[] = new double[2];
//            dis[0] = audioFeature_new.getDistance(audioFeature_o);
//            dis[1] = audioFeature_new2.getDistance(audioFeature_o);
//
//            for (int i = 0; i < dis.length; i++) {
//                System.out.println("Distance " + i + " : " + dis[i]);
//            }
//        } catch (Exception e) {
//        }
    }
}
