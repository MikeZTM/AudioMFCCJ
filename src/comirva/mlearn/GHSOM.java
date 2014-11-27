package comirva.mlearn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import comirva.data.DataMatrix;
import comirva.exception.SizeMismatchException;
import comirva.mlearn.ghsom.GhSomPrototypeFinder;

public class GHSOM extends SOM {
	
	private static final long serialVersionUID = 2947141785259199673L;
	
	private static final int POS_UL = 0;
	private static final int POS_U = 1;
	private static final int POS_UR = 2;
	private static final int POS_R = 3;
	private static final int POS_DR = 4;
	private static final int POS_D = 5;
	private static final int POS_DL = 6;
	private static final int POS_L = 7;
	
	public static final int NA_MAX_SIZE = 3;
	public static final int NA_MAX_DEPTH = 0;
	
	private static int createdSubSOMs = 0;
	
	private GHSOM parent;
	private double overallDeviation;

	private SortedMap<Integer, GHSOM> subMaps = new TreeMap<Integer, GHSOM>();
	
	//maps the index of a data row to the index of the parent map
	//required for correct labeling
	private Map<Integer, Integer> parentIndices;
	
	//store mqe of node to avoid recalculations
	private Map<Integer, Double> mapUnitMQEs = new HashMap<Integer, Double>();
	//store mean of node to avoid recalculations
	private Map<Integer, Vector<Double>> mapUnitMeans = new HashMap<Integer, Vector<Double>>();
	
	//configuration of all SOM's
	private double growThreshold = 0.6;
	private double expandThreshold = 0.1;
	private int initMethod = SOM.INIT_RANDOM;
	private int initNumberOfRows = 2;
	private int initNumberOfColumns = 2;
	
	private int maxSize = Integer.MAX_VALUE;
	private int maxDepth = Integer.MAX_VALUE;
	
	private int hierarchyDepth;
	
	private boolean onlyFirstCircular = false;
	
	private boolean orientated = true;
	
	private boolean onlyOneEntryPerNode = false;
	
	private GhSomPrototypeFinder prototypor;
	
	//only for GUI purposes
	private boolean calculationReady = false;
	
	public GHSOM(DataMatrix trainData) {
		super(trainData, 1, 1);
		this.parent = null;
		this.hierarchyDepth = 0;
		createdSubSOMs = 0;
	}

	/*
	 * Constructor of the created subSOMs
	 * */
	private GHSOM(DataMatrix trainData, GHSOM parent) {
		super(trainData, parent.initNumberOfRows, parent.initNumberOfColumns);
		this.parent = parent;
		this.hierarchyDepth = parent.hierarchyDepth + 1;
		this.overallDeviation = parent.overallDeviation;
	}
	
	private GHSOM createSubSOM(DataMatrix dataMatrix, HashMap<Integer, Integer> newParendIndices) {
		return createSubSOM(dataMatrix, newParendIndices, -1);
	}
	
	private GHSOM createSubSOM(DataMatrix dataMatrix, HashMap<Integer, Integer> newParendIndices, int mapUnitIndex) {
		GHSOM newSOM = new GHSOM(dataMatrix, this);
		newSOM.setInitMethod(initMethod);
		newSOM.setInitNumberOfColumns(initNumberOfColumns);
		newSOM.setInitNumberOfRows(initNumberOfRows);
		newSOM.setTrainingLength(trainingLength);
		newSOM.setGrowThreshold(growThreshold);
		newSOM.setExpandThreshold(expandThreshold);
		newSOM.setMaxDepth(maxDepth);
		newSOM.setMaxSize(maxSize);
		newSOM.setPrototypor(prototypor);
		newSOM.setOrientated(orientated);
		newSOM.setCoOccMatrix(coOccMatrix);
		newSOM.setCoOccMatrixLabels(coOccMatrixLabels);
		newSOM.setOnlyOneEntryPerNode(onlyOneEntryPerNode);
		
		newSOM.setOnlyFirstCircular(onlyFirstCircular);
		if(!onlyFirstCircular || hierarchyDepth == 0)
			newSOM.setCircular(circular);
		
		newSOM.parentIndices = newParendIndices;
		createdSubSOMs++;

		if(orientated && hierarchyDepth > 0)
			newSOM.initOrientated(mapUnitIndex);
		else
			newSOM.init(initMethod);
		return newSOM;
	}

	/** 
	 * Trains the GHSOM using the method given in the parameter <code>method</code>.
	 * 
	 * @param methodValue	the training method used
	 * @param length	the training length in epochs
	 * @see comirva.mlearn.SOM#TRAIN_SEQ
	 * @see comirva.mlearn.SOM#TRAIN_BATCH
	 */
	@Override
	public void train(int methodValue, int length) {
		//log("Start GHSOM training.");
		this.trainingLength = length;
		this.method = methodValue;
		//initialize single codebook unit of first dummy layer map
		//value is the mean of all data items

		try {
			codebook.insertRow(calculateMean(data), 0);
			createVoronoiSet();
			overallDeviation = calculateMQE();
			//log("Data Deviation " + overallDeviation, true);
		} catch (SizeMismatchException e) {
			e.printStackTrace();
		}
		GHSOM newSOM = createSubSOM(data, null);
		subMaps.put(new Integer(0), newSOM);
		//log("Train first subSOM.");
		newSOM.trainGHSOM();
		//log("GHSOM training complete.");
		//log("Created subSOMs: " + createdSubSOMs);
		//log("Cutted subSOMs: " + cuttedSubSOMs);
		calculationReady = true;
	}
	
	private void trainGHSOM() {
		executeTraining();
		createVoronoiSet();
		double mqe = calculateMQE();
		double mapMQE = calculateMQE(calculateMean(data), data);
		//grow SOM until mqe is smaller than :growThreshold:% of the parent unit's mqe (i.e. the dev of this map)
		while(parent != null 
				&& growThreshold * mapMQE < mqe 
				&& intMUCols * intMURows < maxSize
				&& data.getNumberOfRows() >= this.codebook.getNumberOfRows()) {
			
			try {
				//log("Grow current SOM. (threshold: " + growThreshold + 
					//	" * " + dataMQE + " < " + mqe + ")");
				grow();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//printCodebook("after growing");
			executeTraining();
			createVoronoiSet();
			mqe = calculateMQE();
			//printCodebook("after growing and training");
		}
		//expand mapUnits with mqe still higher than :fractionWidth:% 
		//of the parent mqe and train those maps recursive
		for(int i = 0; i < intMUCols * intMURows; i++) {
			if(onlyOneEntryPerNode && this.voronoiSet.elementAt(i).size() > 1
					|| expandThreshold * overallDeviation < calculateMQE(i) && hierarchyDepth < maxDepth) {
				//log("Expand unit to subSOM. (threshold: " + expandThreshold + 
				//	" * " + overallDeviation + " < " + tempMQE + ")");
				expandUnit(i);
				subMaps.get(Integer.valueOf(i)).trainGHSOM();
			}
		}
	}

	private void executeTraining() {
		this.mapUnitMQEs.clear();
		this.mapUnitMeans.clear();
		this.trainSequential();
	}
	/**
	 * This method allows the SOM to grow. First an error unit is determined (the
	 * one with the highest mqe). Between the given error unit
	 * and its most dissimilar neighbor a new row or column is inserted.
	 * The initialization of the new units is performed as the average of
	 * the neighbor units.
	 * Able to handle circular SOMs
	 * @throws Exception 
	 */
	private void grow() throws Exception {
		int errorUnit = determineErrorUnit();
		int dissimilarUnit = getMostDissimilarNeighbor(errorUnit);

		int errorUnitRow = errorUnit % intMURows;
		int errorUnitCol = (int)Math.floor(errorUnit / intMURows);
		int dissUnitRow = dissimilarUnit % intMURows;
		int dissUnitCol = (int)Math.floor(dissimilarUnit / intMURows);
		
		if(errorUnitRow == dissUnitRow) {
			insertColumnBetween(errorUnitCol, dissUnitCol);
		} else if(errorUnitCol == dissUnitCol){
			insertRowBetween(errorUnitRow, dissUnitRow);
		} else
			throw new Exception("Fatal error while growing map.");
	
	}
	
	/**
	 * Insert a row in the codebook, between the two row indices.
	 * Asserts that the parameter rows are neighbors.
	 * Initializes the new map units.
	 * @param row1
	 * @param row2
	 */
	private void insertRowBetween(int row1, int row2) {
		//log("insert row between " + row1 + " " + row2);
		int minRow = Math.min(row1, row2);
		int maxRow = Math.max(row1, row2);
		
		//insert backwards to maintain correct indices of required map units
		for(int i = intMUCols -1; i > -1; i--) {
			Vector<Double> neighbor1 = codebook.getRow(i*intMURows + row1);
			Vector<Double> neighbor2 = codebook.getRow(i*intMURows + row2);
			try {
				Vector<Double> newUnit = calculateMean(neighbor1, neighbor2);
				
				//case possible if SOM circular
				if(intMURows > 2 && minRow == 0 && maxRow == intMURows -1) 
					codebook.insertRow(newUnit, i*intMURows + maxRow + 1);
				else
					codebook.insertRow(newUnit, i*intMURows + maxRow);
				
			} catch (SizeMismatchException e) {
				e.printStackTrace();
			}
		}
		intMURows++;
	}

	
	/**
	 * Insert a column in the codebook, between the two column indices.
	 * Asserts that the parameter columns are neighbors.
	 * Initializes the new map units.
	 * @param col1
	 * @param col2
	 */
	private void insertColumnBetween(int col1, int col2) {
		//log("insert column between " + col1 + " " + col2);
		int minCol = Math.min(col1, col2);
		int maxCol = Math.max(col1, col2);
		
		int insertCount = 0;
		for(int i = 0; i < intMURows; i++) {
			//log("neighbor1: " + (intMURows*minCol + i));
			//log("neighbor2: " + (intMURows*maxCol + i + insertCount));
			Vector<Double> neighbor1 = codebook.getRow(intMURows*minCol + i);
			Vector<Double> neighbor2 = codebook.getRow(intMURows*maxCol + i + insertCount);
			try {
				Vector<Double> newUnit = calculateMean(neighbor1, neighbor2);
				
				//case possible if SOM circular
				if(intMUCols > 2 && minCol == 0 && maxCol == intMUCols -1) 
					codebook.insertRow(newUnit, maxCol*intMURows + intMURows + i);
				else {
					try {
						//log("insert at " + (maxCol*intMURows + i));
						codebook.insertRow(newUnit, maxCol*intMURows + i);
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						//log("col1: " + col1 + ", col2: "+ col2);
						//log("som size: " + codebook.getNumberOfRows());
						//log("som rows: " + intMURows + ", som cols: " + intMUCols);	
					}
					insertCount++;
				}
				
			} catch (SizeMismatchException e) {
				e.printStackTrace();
			}
		}
		intMUCols++;
	}
	
	private int determineErrorUnit() {
		double maxMQE = 0;
		int errorUnit = -1;

		for(int i = 0; i < intMUCols * intMURows; i++) {
			double mqe = calculateMQE(i);
			if(mqe > maxMQE) {
				maxMQE = mqe;
				errorUnit = i;
			}
		}
		return errorUnit;
	}
	
	private int getMostDissimilarNeighbor(int mapunitIndex) {
		double maxDistance = 0;
		int dissimilarUnit = -1;

		//determine most dissimilar neighbor
		for(int i = 0; i < intMUCols * intMURows; i++) {
			if(i != mapunitIndex && mapunitDistance(mapunitIndex, i) == 1) {
				try {
					double distance = this.euclideanDistance(
							this.codebook.getRow(i), 
							this.codebook.getRow(mapunitIndex));
					if(distance >= maxDistance) {
						maxDistance = distance;
						dissimilarUnit = i;
					}
				} catch (SizeMismatchException e) {
					e.printStackTrace();
				}
			}
		}
		return dissimilarUnit;
	}
	
	/**
	 * expands a mapUnit, creating a new som with the corresponding voronoi
	 * data
	 */
	private void expandUnit(int mapUnitIndex) {
		//log("expand unit " + mapUnitIndex);
		Vector<Integer> mappedUnits = this.voronoiSet.elementAt(mapUnitIndex);
		DataMatrix newData = new DataMatrix();
		HashMap<Integer, Integer> newParentIndices = new HashMap<Integer, Integer>();
		//log("unit gets " + mappedUnits.size() + " data points");
		try {
			for(int i = 0; i < mappedUnits.size(); i++) {
				Integer index = mappedUnits.elementAt(i);
				newData.insertRow(data.getRow(index.intValue()) , i);
				newParentIndices.put(new Integer(i), index);
			}
		} catch (SizeMismatchException e) {
			e.printStackTrace();
		}

		subMaps.put(new Integer(mapUnitIndex), createSubSOM(newData, newParentIndices, mapUnitIndex));
	}
	
	
	/**
	 * Calculates the Mean Quantization Error of a SOM.
	 * Calculated as a weighted sum of its map units.
	 */
	private double calculateMQE() {
		double sum = 0.0;
		for(int i = 0; i < this.codebook.getNumberOfRows(); i++) {
			double mqe = calculateMQE(i) * this.voronoiSet.get(i).size();
			sum += mqe;
		}
		//log("result: " + sum + " / " + this.data.getNumberOfRows());
		return sum / this.data.getNumberOfRows();
	}
	
	/**
	 * Calculates the Mean Quantization Error of a map unit.
	 */
	private double calculateMQE(int mapUnitIndex) {
		Double cachedResult = this.mapUnitMQEs.get(Integer.valueOf(mapUnitIndex));
		if(cachedResult != null)
			return cachedResult.doubleValue();
		
		Vector<Integer> mappedUnits = this.voronoiSet.elementAt(mapUnitIndex);
		double sum = 0.0;
		if(mappedUnits.size() == 0)
			return sum;
		
		Vector<Double> mean = this.mapUnitMeans.get(Integer.valueOf(mapUnitIndex));
		if(mean == null) {
			mean = calculateVoronoiMean(mappedUnits);
			this.mapUnitMeans.put(Integer.valueOf(mapUnitIndex), mean);
		}
			
		for(int i = 0; i < mappedUnits.size(); i++) {
			try {
				Integer index = mappedUnits.elementAt(i);
				sum += euclideanDistance(mean, data.getRow(index.intValue()));
			} catch (SizeMismatchException e) {
				e.printStackTrace();
			}
		}
		//log("mqe of MU " + mapUnitIndex + ": " + sum +"/"+ mappedUnits.size());
		double result = sum / mappedUnits.size();
		//cache the result
		this.mapUnitMQEs.put(Integer.valueOf(mapUnitIndex), Double.valueOf(result));
		return result;
	}

	/**
	 * Calculates the Mean Quantization Error.
	 */
	private double calculateMQE(Vector<Double> unit, DataMatrix dataMatrix) {
		int dataSize = dataMatrix.getNumberOfRows();
		double sum = 0.0;
		
		if(dataSize == 0)
			return sum;
				
		
		for(int i = 0; i < dataSize; i++) {
			try {
				sum += euclideanDistance(unit, dataMatrix.getRow(i));
			} catch (SizeMismatchException e) {
				e.printStackTrace();
			}
		}
		return sum / dataSize;
	}
	
	private Vector<Double> calculateMean(DataMatrix dataMatrix) {
		Vector<Vector<Double>> inputVectors = new Vector<Vector<Double>>();
		for(int i = 0; i < dataMatrix.getNumberOfRows(); i++)
			inputVectors.add(dataMatrix.getRow(i));
		return calculateMean(inputVectors);		
	}
	
	public Vector<Double> calculateVoronoiMean(Vector<Integer> inputVoronoiSet) {
		if(inputVoronoiSet == null || inputVoronoiSet.isEmpty())
			return null;

		Vector<Vector<Double>> inputVectors = new Vector<Vector<Double>>();
		for(Integer index: inputVoronoiSet)
			inputVectors.add(data.getRow(index.intValue()));	
		return calculateMean(inputVectors);	
	}
	
	private Vector<Double> calculateMean(Vector<Double> a, Vector<Double> b) {
		Vector<Vector<Double>> inputVectors = new Vector<Vector<Double>>();
		inputVectors.add(a);
		inputVectors.add(b);
		return calculateMean(inputVectors);
	}
	
	public static Vector<Double> calculateMean(Vector<Vector<Double>> inputVectors) {	
		if(inputVectors == null || inputVectors.isEmpty())
			return null;
		
		int dataLength = inputVectors.get(0).size();
		Vector<Double> mean = new Vector<Double>(dataLength);
		for(int i = 0; i < dataLength; i++)
			mean.add(new Double(0.0));

		for (Vector<Double> currentVector: inputVectors) {
			for(int j=0; j < currentVector.size(); j++) {
				Double value = currentVector.elementAt(j);
				mean.setElementAt(Double.valueOf(mean.elementAt(j).doubleValue() + value.doubleValue()), j);
			}
		}
		for(int i = 0; i < mean.size(); i++)
			mean.setElementAt(Double.valueOf(mean.elementAt(i).doubleValue() / inputVectors.size()), i);
		return mean;		
	}
	
	private Vector<Double> cloneVector(Vector<Double> toClone) {
		Vector<Double> clone = new Vector<Double>();
		for(Double d: toClone)
			clone.add(new Double(d.doubleValue()));
		return clone;
	}
	

	
	/*
	private void log(String s) {
		System.out.println(s);
	}*/

	/*
	private void printCodebook(String s) {
		log(" --- current codebook " + s + " --- ");
		for(int i = 0; i < codebook.getNumberOfRows(); i++) {
			log(i + ": " + codebook.getRow(i).toString());
		}
		log(" --- current codebook end --- ");
	}*/
	
	/*
	private void printData() {
		log(" --- current data: " + data.getNumberOfRows() + " items --- ");
		for(int i = 0; i < data.getNumberOfRows(); i++) {
			log(i + ": " + data.getRow(i).toString());
		}
		log(" --- current data end --- ");
	}*/
	
	public GHSOM getSubSOM(int nodeNumber) {
		if(subMaps.containsKey(new Integer(nodeNumber)))
			return subMaps.get(new Integer(nodeNumber));
		return null;
	}
	
	public Collection<GHSOM> getChildren() {
		return subMaps.values();
	}

	public GHSOM getParent() {
		return parent;
	}
	
	private Integer getOriginalIndex(Integer index) {
		if(parentIndices == null || parent == null)
			return index;
		return parent.getOriginalIndex(parentIndices.get(index));
	}
	
	@Override
	public Vector<String> getLabels() {
		if(parent == null)
			return labels;
		return parent.getLabels();
	}
	
	@Override
	public String getLabel(int dataItemIndex) {
		Integer originalIndex = getOriginalIndex(new Integer(dataItemIndex));
		Vector<String> originalLabels = getLabels();
		if (originalLabels != null && !originalLabels.isEmpty() && 
				(originalLabels.elementAt(originalIndex.intValue()) != null)) 
			return originalLabels.elementAt(originalIndex.intValue());

		return originalIndex.toString(); 
	}
	
	@Override
	public Vector<String> getAltLabels() {
		if(parent == null)
			return altLabels;
		return parent.getAltLabels();
	}
	
	@Override
	public String getAltLabel(int dataItemIndex) {
		Integer originalIndex = getOriginalIndex(new Integer(dataItemIndex));
		Vector<String> originalAltLabels = getAltLabels();
		if (originalAltLabels != null && !originalAltLabels.isEmpty() && 
				(originalAltLabels.elementAt(originalIndex.intValue()) != null)) 
			return originalAltLabels.elementAt(originalIndex.intValue());

		return originalIndex.toString(); 
	}
	
	@Override
	public DataMatrix getCoOccMatrix() {
		if(parent == null)
			return coOccMatrix;
		return parent.getCoOccMatrix();
	}
	
	@Override
	public Vector<String> getCoOccMatrixLabels() {
		if(parent == null)
			return coOccMatrixLabels;
		return parent.getCoOccMatrixLabels();
	}

	public void setExpandThreshold(double expandThreshold) {
		this.expandThreshold = expandThreshold;
	}

	public void setGrowThreshold(double growThreshold) {
		this.growThreshold = growThreshold;
	}

	public void setInitMethod(int initMethod) {
		this.initMethod = initMethod;
	}

	public void setInitNumberOfColumns(int initNumberOfColumns) {
		this.initNumberOfColumns = initNumberOfColumns;
	}

	public void setInitNumberOfRows(int initNumberOfRows) {
		this.initNumberOfRows = initNumberOfRows;
	}

	public int getHierarchyDepth() {
		return hierarchyDepth;
	}

	public GhSomPrototypeFinder getPrototypor() {
		if(parent == null)
			return prototypor;
		return parent.getPrototypor();
	}

	public void setPrototypor(GhSomPrototypeFinder prototypor) {
		this.prototypor = prototypor;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public boolean isOnlyFirstCircular() {
		return onlyFirstCircular;
	}

	public void setOnlyFirstCircular(boolean onlyFirstCircular) {
		this.onlyFirstCircular = onlyFirstCircular;
	}

	public boolean isCalculationReady() {
		return calculationReady;
	}

	public boolean isOrientated() {
		return orientated;
	}

	public void setOrientated(boolean orientated) {
		this.orientated = orientated;
	}
	
	//special initialisations, at last ;)
	private void initOrientated(int mapUnitIndex) {
		//System.out.println("Orientierung: depth-"+ hierarchyDepth + " code book: " + mapUnitIndex);
		Vector<Double> ownValue = parent.getCodebook().getRow(mapUnitIndex);
		
		Vector<Double> upperLeft = getCornerValue(
				getNeighborSOMValue(mapUnitIndex, POS_L), 
				getNeighborSOMValue(mapUnitIndex, POS_U), 
				getNeighborSOMValue(mapUnitIndex, POS_UL),
				ownValue);
		//System.out.println(upperLeft);
		Vector<Double> upperRight = getCornerValue(
				getNeighborSOMValue(mapUnitIndex, POS_R), 
				getNeighborSOMValue(mapUnitIndex, POS_U), 
				getNeighborSOMValue(mapUnitIndex, POS_UR),
				ownValue);
		//System.out.println(upperRight);
		Vector<Double> lowerLeft = getCornerValue(
				getNeighborSOMValue(mapUnitIndex, POS_L), 
				getNeighborSOMValue(mapUnitIndex, POS_D), 
				getNeighborSOMValue(mapUnitIndex, POS_DL),
				ownValue);
		//System.out.println(lowerLeft);
		Vector<Double> lowerRight = getCornerValue(
				getNeighborSOMValue(mapUnitIndex, POS_R), 
				getNeighborSOMValue(mapUnitIndex, POS_D), 
				getNeighborSOMValue(mapUnitIndex, POS_DR),
				ownValue);
		//System.out.println(lowerRight);
		this.initWithCorners(upperLeft, upperRight, lowerLeft, lowerRight);		
	}
	
	private Vector<Double> getCornerValue(Vector<Double> v1, Vector<Double> v2, Vector<Double> v3, Vector<Double> ownValue) {
		Vector<Vector<Double>> influences = new Vector<Vector<Double>>();
		if(v1 != null)
			influences.add(v1);
		if(v2 != null)
			influences.add(v2);
		if(v3 != null)
			influences.add(v3);
		
		if(influences.isEmpty())
			return cloneVector(ownValue);
		if(influences.size() == 1)
			return cloneVector(influences.get(0));
		return calculateMean(influences);
	}

	//TODO: integrate circularity, if needed
	private Vector<Double> getNeighborSOMValue(int ownPosition, int neighborPosition) {
		DataMatrix parentCodebook = parent.getCodebook();
		int parentRows = parent.getNumberOfRows();
		int parentCols = parent.getNumberOfColumns();
		int ownRow = ownPosition / parentCols;
		int ownCol = ownPosition % parentCols;
		
		switch(neighborPosition) {
		case POS_D:
			if(ownRow != parentRows - 1)
				return parentCodebook.getRow(ownPosition + parentCols);
			break;
		case POS_DL:
			if(ownRow != parentRows - 1 && ownCol != 0)
				return parentCodebook.getRow(ownPosition + parentCols - 1);
			break;
		case POS_DR:
			if(ownRow != parentRows - 1 && ownCol != parentCols - 1)
				return parentCodebook.getRow(ownPosition + parentCols + 1);
			break;
		case POS_L:
			if(ownCol != 0)
				return parentCodebook.getRow(ownPosition - 1);
			break;
		case POS_R:
			if(ownCol != parentCols - 1)
				return parentCodebook.getRow(ownPosition + 1);
			break;
		case POS_U:
			if(ownRow != 0)
				return parentCodebook.getRow(ownPosition - parentCols);
			break;
		case POS_UL:
			if(ownRow != 0 && ownCol != 0)
				return parentCodebook.getRow(ownPosition - parentCols - 1);
			break;
		case POS_UR:
			if(ownRow != 0 && ownCol != parentCols - 1)
				return parentCodebook.getRow(ownPosition - parentCols + 1);
			break;
		}
		return null;
	}

	public boolean isOnlyOneEntryPerNode() {
		return onlyOneEntryPerNode;
	}

	public void setOnlyOneEntryPerNode(boolean onlyOneEntryPerNode) {
		this.onlyOneEntryPerNode = onlyOneEntryPerNode;
	}
	
	public List<String> getSequentialList() {
		List<String> list = new ArrayList<String>();
		GHSOM tempSubSOM = null;
		for(int i = 0; i < intMUCols * intMURows; i++) {
			tempSubSOM = subMaps.get(Integer.valueOf(i));
			if(tempSubSOM != null) 
				list.addAll(tempSubSOM.getSequentialList());
			else 
				for(Integer entryKey: this.voronoiSet.elementAt(i))
					list.add(getLabel(entryKey.intValue()));
		}
		return list;
	}

}
