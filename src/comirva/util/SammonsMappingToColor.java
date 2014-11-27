package comirva.util;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.IOException;

import comirva.mlearn.*;
import cp.util.*;


public class SammonsMappingToColor {
	private static BufferedImage cielab = null;
	private static int xMax;
	private static int yMax;

	public static Color[] getColorsForFeatures(double[][] features) {
		Color[] colors = new Color[features.length];
		if (cielab == null) {
			for (int i=0; i<colors.length; i++) {
				colors[i] = Color.WHITE;
			}
			return colors;
		}
		double[][] dists = Vec.getDistanceMatrix(features);
		
		SammonsMapping sm = new SammonsMapping(dists);
//		for (int i=0; i<1000; i++) {
		sm.iterate(10000, 0.000000001, 60000);
//			System.out.println(sm.getError());
//		}
		double[][] smfeatures = sm.getLowcoords();
		
		// find min and max of both dimensions
		double FirstDimMin = Double.POSITIVE_INFINITY;
		double FirstDimMax = Double.NEGATIVE_INFINITY;
		double SecondDimMin = Double.POSITIVE_INFINITY;
		double SecondDimMax = Double.NEGATIVE_INFINITY;
		for (int i=0; i<features.length; i++) {
			if (features[i].length>1) {
				if (smfeatures[i][0]<FirstDimMin)
					FirstDimMin = smfeatures[i][0];
				if (smfeatures[i][0]>FirstDimMax)
					FirstDimMax = smfeatures[i][0];
				if (smfeatures[i][1]<SecondDimMin)
					SecondDimMin = smfeatures[i][1];
				if (smfeatures[i][1]>SecondDimMax)
					SecondDimMax = smfeatures[i][1];
			}
		}
		
		// scale values to dimensions of image and assign color from corresponding pixel
		for (int i=0; i<features.length; i++) {
			if (features[i].length>1) {
				// first dim
				int xCoord = (int)(((double)(xMax-1))*((smfeatures[i][0]-FirstDimMin)/(FirstDimMax-FirstDimMin)));
				int yCoord = (int)(((double)(yMax-1))*((smfeatures[i][1]-SecondDimMin)/(SecondDimMax-SecondDimMin)));

				try {
//					Color c = new Color(cielab.getRGB(xCoord, yCoord));
					Color c = new Color(xCoord, 255-yCoord, yCoord);
					colors[i] = c;
//					System.out.println("Color at "+xCoord+","+yCoord+": "+c.toString());
				}
				catch (Exception e) {
//					System.err.println("Coordinate out of bounds! "+xCoord+","+yCoord);
				}
			}
			else {
				colors[i] = Color.WHITE;
			}
		}

		return colors;
	}
	
	static {
		// load cielab 50% color space image
		try {
			cielab = ImageIO.read(SammonsMappingToColor.class.getResource("CIELab_neutral_luminance.png"));
			xMax = cielab.getWidth();
			yMax = cielab.getHeight();
//			System.out.println("CIELab image: "+xMax+"x"+yMax);
		} catch (Exception e) {
			System.err.println("can't read CIELab image.");
		}
	}
}
