package comirva.audio.feature;

import comirva.data.*;
import comirva.audio.util.math.Matrix;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import comirva.audio.XMLSerializable;

/**
 * <b>Fluctuation Pattern</b>
 *
 * <p>Description: </p>
 * The Fluctuation Pattern is an audio feature trying to decribe the rhythmic
 * structure of a song. This is done by summarizing the loudness changes in
 * various frequency bands.
 *
 * @see comirva.audio.extraction.FluctuationPatternExtractor
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class FluctuationPattern extends AudioFeature implements XMLSerializable
{
  protected Matrix m;


  /**
   * A matrix describing the rhythmic structure of a song in various frequency
   * bands known as fluctuation patterns.
   *
   * @param m Matrix the fluctuation pattern matrix
   */
  public FluctuationPattern(Matrix m)
  {
    super();
    this.m = m;
  }


  /**
   * Used for XML Serialization only.
   */
  protected FluctuationPattern()
  {
    super();
  }


  /**
   * Returns the fluctuation pattern matrix converted to an object of type
   * <code>DataMatrix</code>.
   *
   * @see comirva.data.DataMatrix
   * @return DataMatrix the fluctuation pattern matrix
   */
  public DataMatrix getDataMatrix()
  {
    DataMatrix dm = new DataMatrix("Fluctuation Pattern");

    //convert matrix to data matrix
    for(int row = 0; row < m.getRowDimension(); row++)
    {
      dm.addValue(new Double(m.get(row, 0)), true);
      for(int col = 1; col < m.getColumnDimension(); col++)
        dm.addValue(new Double(m.get(row, col)));
    }

    return dm;
  }


  /**
   * Returns the fluctuation pattern matrix.
   *
   * @return Matrix the fluctuation pattern matrix
   */
  public Matrix getMatrix()
  {
    return m;
  }

  /**
   * Returns the fluctuation pattern matrix as a double array.
   *
   * @return double[] the fluctuation pattern matrix, row packed
   */
  public double[] getAsArray()
  {
    return m.getRowPackedCopy();
  }


  /**
   * Returns the fluctuation pattern matrix as a matrix array.
   *
   * @return double[][] the fluctuation pattern matrix
   */
  public double[][] getMatrixAsArray()
  {
    return m.getArrayCopy();
  }


  /**
   * Computes the distance between two fluctuation patterns representing two
   * songs. This is done by computing the euclidian distance of the two
   * fluctuation pattern matrices.
   *
   * @param f AudioFeature another fluctuation pattern feature
   * @return double the euclidian distance between two fluctuation pattern
   *                matrices
   *
   * @throws ClassCastException if the passed <code>AudioFeature<code> is
   *                                   not an object of type
   *                                   <code>FluctuationPattern</code>
   */
  public double getDistance(AudioFeature f) throws ClassCastException
  {
    //check for compatibility
    if(f.getType() != this.getType())
      throw new ClassCastException("features of different type are not compareable");

    FluctuationPattern fp = (FluctuationPattern) f;


    //compute euclidian distance
    Matrix n = fp.getMatrix();

    //first compute difference of each element
    n = m.minus(n);

    double[][] a = n.getArray();

    //sum up the the squared difference of each element
    double sum = 0;
    for(int i = 0; i < a.length; i++)
      for(int j = 0; j < a[i].length; j++)
        sum += a[i][j]*a[i][j];

    //take the square root of the sum
    return Math.sqrt(sum);
  }


  /**
   * Writes the xml representation of this object to the xml ouput stream.<br>
   * <br>
   * There is the convetion, that each call to a <code>writeXML()</code> method
   * results in one xml element in the output stream.
   *
   * @param writer XMLStreamWriter the xml output stream
   *
   * @throws IOException raised, if there are any io troubles
   * @throws XMLStreamException raised, if there are any parsing errors
   */
  public void writeXML(XMLStreamWriter writer) throws IOException, XMLStreamException
  {
    writer.writeStartElement("feature");
    writer.writeAttribute("type", getClassName());
    m.writeXML(writer);
    writer.writeEndElement();
  }


  /**
   * Reads the xml representation of an object form the xml input stream.<br>
   * <br>
   * There is the convention, that <code>readXML()</code> starts parsing by
   * checking the start tag of this object and finishes parsing by checking the
   * end tag. The caller has to ensure, that at method entry the current token
   * is the start tag. After the method call it's the callers responsibility to
   * move from the end tag to the next token.
   *
   * @param parser XMLStreamReader the xml input stream
   *
   * @throws IOException raised, if there are any io troubles
   * @throws XMLStreamException raised, if there are any parsing errors
   */
  public void readXML(XMLStreamReader parser) throws IOException, XMLStreamException
  {
    parser.require(XMLStreamReader.START_ELEMENT, null, "feature");

    parser.nextTag();
    m = new Matrix(0, 0);
    m.readXML(parser);
  }
}
