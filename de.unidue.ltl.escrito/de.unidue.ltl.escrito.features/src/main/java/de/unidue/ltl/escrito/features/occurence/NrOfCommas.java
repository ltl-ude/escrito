package de.unidue.ltl.escrito.features.occurence;

import java.util.Set;

import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * Counts the appearance commas
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"})
public class NrOfCommas extends FeatureExtractorResource_ImplBase
	implements FeatureExtractor
{

	public static final String NR_OF_COMMAS = "nrOfCommas";
	
	@Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget target) 
			throws TextClassificationException
	{
		
		String text = jcas.getDocumentText();
		//length of split is minimum 1
		int nrOfCommas = text.split(",").length-1;
		int nrOfTokens = JCasUtil.select(jcas, Token.class).size();

		//Normalization on total count of words
		double ratio = (double) nrOfCommas/nrOfTokens;
		return new Feature(NR_OF_COMMAS, ratio, FeatureType.NUMERIC).asSet();
	}
}
