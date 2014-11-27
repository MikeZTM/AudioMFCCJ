package comirva.util.external.dopler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import comirva.data.DataMatrix;
import comirva.exception.NoMatrixException;
import comirva.exception.SizeMismatchException;
import comirva.io.MatrixDataFileLoaderThread;
import comirva.io.MetaDataFileLoaderThread;
import comirva.mlearn.GHSOM;
import comirva.mlearn.SOM;

public class EvalUtil {

	public static final String TEST_GHSOMS_PATH = "D:/Diplomarbeit/Ghsoms_combined/";
	
	private static final String DATA_PATH = "C:/Dokumente und Einstellungen/Maxwell/Eigene Dateien/pca30_combined.dat";
	private static final String METADATA_PATH = "C:/Dokumente und Einstellungen/Maxwell/Eigene Dateien/genres2545.dat";
	
	public static void main(String[] args) {
		for(int i = 0; i < 20; i++) {
			System.out.println("create ghsom " + i);
			storeGHSOM(createGHSOM(DATA_PATH, METADATA_PATH), TEST_GHSOMS_PATH + "ghsom" + i + ".ghs");
		}
	}
	
	public static List<GHSOM> getTestGHSOMs(int amount) {
		if(amount > 20)
			amount = 20;
		List<GHSOM> result = new ArrayList<GHSOM>();
		while(amount-- > 0) {
			result.add(loadGHSOM(TEST_GHSOMS_PATH + "ghsom" + amount + ".ghs"));
		}
		return result;
	}
	
	public static GHSOM loadGHSOM(String path) {
		GHSOM ghsom = null;
		try {
			File fileData = new File(path);
			FileInputStream in = new FileInputStream(fileData);
			ObjectInputStream s = new ObjectInputStream(in);
			ghsom = (GHSOM) s.readObject();
			s.close();
			in.close();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return ghsom;
	}
	
	public static void storeGHSOM(GHSOM ghsom, String path) {
		File file = new File(path);
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			ObjectOutputStream s = new ObjectOutputStream(out);
			s.writeObject(ghsom);
			s.flush();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void stringToFile(String stringToWrite, String path) {
		File file = new File(path);
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(stringToWrite.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static DataMatrix loadDataMatrix(String dataPath) {
		MatrixDataFileLoaderThread dfl = new MatrixDataFileLoaderThread(new File(dataPath));
		try {
			return dfl.getMatrixFromFile();
		} catch (NoMatrixException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public static Vector loadMetadataMatrix(String dataPath) {
		MetaDataFileLoaderThread dfl = new MetaDataFileLoaderThread(new File(dataPath));
		return dfl.getMetaDataFromFile();
	}
	
	@SuppressWarnings("unchecked")
	public static GHSOM createGHSOM(String dataPath, String metadataPath) {
		DataMatrix data = loadDataMatrix(dataPath);
		Vector metadata = loadMetadataMatrix(metadataPath);
		GHSOM ghsom = new GHSOM(data);
		try {
			ghsom.setLabels(metadata);
		} catch (SizeMismatchException e) {
			e.printStackTrace();
		}
		
		ghsom.setGrowThreshold(0.5);
		ghsom.setExpandThreshold(0.1);
		ghsom.setInitMethod(SOM.INIT_RANDOM);
		ghsom.setInitNumberOfColumns(1);
		ghsom.setInitNumberOfRows(5);
		ghsom.setCircular(false);
		ghsom.setOnlyFirstCircular(true);
		ghsom.setOrientated(true);
		ghsom.setMaxSize(20);
		ghsom.setMaxDepth(Integer.MAX_VALUE);
		ghsom.setOnlyOneEntryPerNode(true);
		ghsom.train(SOM.TRAIN_SEQ, 10);
		return ghsom;
	}
}
