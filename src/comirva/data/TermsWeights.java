/*
 * Created on 30.05.2007
 */
package comirva.data;

import java.util.ArrayList;

/** 
 * This is a very simple helper class to 
 * represent (term, weight)-pairs 
 * for various term weighting functions. 
 */ 
public class TermsWeights {
	ArrayList terms = new ArrayList();
	ArrayList weights = new ArrayList();

	public TermsWeights(ArrayList terms, ArrayList weights) {
		this.terms = terms;
		this.weights = weights;
	}

	public ArrayList getTerms() {
		return this.terms;
	}

	public ArrayList getWeights() {
		return this.weights;  
	}

	public float getMaximumWeight() {
		float maxWeight = Float.NEGATIVE_INFINITY; 
		if (this.weights != null) {
			for (int i=0; i<this.weights.size(); i++) {
				float value = ((Float)this.weights.get(i)).floatValue();
				if (value > maxWeight)
					maxWeight = value; 
			}
		} else
			maxWeight = -1;
		return maxWeight;
	}

	public void add(String term, Float weight) {
		this.terms.add(term);
		this.weights.add(weight);
	}

	public void remove(int index)  {
		this.terms.remove(index);
		this.weights.remove(index);
	}

	public int getSize() {
		return this.terms.size();  
	}
	
	public void print() {
		for (int i=0; i<this.terms.size(); i++)
			System.out.println(this.terms.get(i) + " (" + this.weights.get(i) + ")");
	}
}
