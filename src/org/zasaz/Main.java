package org.zasaz;

import comirva.audio.extraction.MFCCExtractor;
import comirva.audio.extraction.TimbreDistributionExtractor;
import comirva.audio.feature.AudioFeature;
import comirva.audio.util.PointList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        try {
            File is = new File("C:\\Users\\Mike\\Indie Rocker_alt1.mp3");
            AudioFeature audioFeature_o = null;
            TimbreDistributionExtractor featureExtractor = new TimbreDistributionExtractor();
            AudioInputStream in;
            in = AudioSystem.getAudioInputStream(is);
            audioFeature_o = (AudioFeature) featureExtractor.calculate(in);
            in.close();

            is = new File("C:\\Users\\Mike\\Indie Rocker_full.mp3");
            AudioFeature audioFeature_new = null;
            in = AudioSystem.getAudioInputStream(is);
            audioFeature_new = (AudioFeature) featureExtractor.calculate(in);
            in.close();

            is = new File("C:\\Users\\Mike\\Indie Rocker_alt1_1.mp3");
            AudioFeature audioFeature_new2 = null;
            in = AudioSystem.getAudioInputStream(is);
            audioFeature_new2 = (AudioFeature) featureExtractor.calculate(in);
            in.close();

            double dis[] = new double[2];
            dis[0] = audioFeature_new.getDistance(audioFeature_o);
            dis[1] = audioFeature_new2.getDistance(audioFeature_o);

            for (int i = 0; i < dis.length; i++) {
                System.out.println("Distance " + i + " : " + dis[i]);
            }
        } catch (Exception e) {
        }
    }
}
