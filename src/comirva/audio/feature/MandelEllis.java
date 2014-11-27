package comirva.audio.feature;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.sun.org.apache.bcel.internal.classfile.Deprecated;
import comirva.audio.util.math.Matrix;
import comirva.audio.XMLSerializable;

/**
 * @author tim
 *
 */
public class MandelEllis extends AudioFeature implements XMLSerializable
{

	/**
	 * "Gaussian Mixture Model for Mandel / Ellis algorithm" This class holds
	 * the features needed for the Mandel Ellis algorithm: One full covariance
	 * matrix, and the mean of all MFCCS.
	 *
	 * @author tim
	 *
	 */
	public static class GmmMe
  {
		final Matrix covarMatrix;
		/** the inverted covarMatrix, stored for computational efficiency */
		final Matrix covarMatrixInv;
		/** a row vector */
		final Matrix mean;

    public GmmMe(Matrix covarMatrix, Matrix mean)
    {
			this.covarMatrix = covarMatrix;
			this.mean = mean;
			covarMatrixInv = covarMatrix.inverse();
		}
	}

	private GmmMe gmmMe; // the feature

	public MandelEllis(GmmMe gmmMe){
		this();
		this.gmmMe = gmmMe;
	}

	protected MandelEllis() {
		super();
	}


	/**
	 * Calculate the Kullback-Leibler (KL) distance between the two GmmMe. (Also
	 * known as relative entropy) To make the measure symmetric (ie. to obtain a
	 * divergence), the KL distance should be called twice, with exchanged
	 * parameters, and the result be added.
	 *
	 * Implementation according to the submission to the MIREX'05 by Mandel and Ellis.
	 * @param gmmMe1
	 *            ME features of song 1
	 * @param gmmMe2
	 *            ME features of song 2
	 * @return the KL distance from gmmMe1 to gmmMe2
	 */
	private float kullbackLeibler(GmmMe gmmMe1, GmmMe gmmMe2){
		final int dim = gmmMe1.covarMatrix.getColumnDimension();

		// calculate the trace-term:
		Matrix tr1 = gmmMe2.covarMatrixInv.times(gmmMe1.covarMatrix);
		Matrix tr2 = gmmMe1.covarMatrixInv.times(gmmMe2.covarMatrix);
		Matrix sum = tr1.plus(tr2);
		float trace = (float)sum.trace();


		// "distance" between the two mean vectors:
		Matrix dist = gmmMe1.mean.minus(gmmMe2.mean);

		// calculate the second brace:
		Matrix secBra = gmmMe2.covarMatrixInv.plus(gmmMe1.covarMatrixInv);

		Matrix tmp1 = dist.transpose().times(secBra);

		// finally, the whole term:
		return 0.5f * (trace - 2*dim + (float)tmp1.times(dist).get(0, 0));

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see comirva.audio.feature.AudioFeature#getDistance(comirva.audio.feature.AudioFeature)
	 */
	@Override
	public double getDistance(AudioFeature f)
  {
    if(!(f instanceof MandelEllis))
    {
			(new Exception("Can only handle AudioFeatures of type Mandel Ellis, not of: "+f)).printStackTrace();
			return -1;
		}
		MandelEllis other = (MandelEllis)f;
		return kullbackLeibler(this.gmmMe, other.gmmMe) +
		kullbackLeibler(other.gmmMe, this.gmmMe);
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
    gmmMe.covarMatrix.writeXML(writer);
    gmmMe.mean.writeXML(writer);
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

    Matrix covarMatrix = new Matrix(0,0);
    covarMatrix.readXML(parser);

    parser.nextTag();

    Matrix mean = new Matrix(0,0);
    mean.readXML(parser);

    this.gmmMe = new GmmMe(covarMatrix, mean);
  }

}
