package de.unidue.ltl.escrito.examples.shortanswer.classification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;

import de.unidue.ltl.escrito.examples.basics.Experiments_ImplBase;
import de.unidue.ltl.escrito.examples.basics.FeatureSettings;
import de.unidue.ltl.escrito.io.shortanswer.Asap2Reader;
import de.unidue.ltl.escrito.io.shortanswer.PowerGradingReader;
import weka.clusterers.SimpleKMeans;

import org.dkpro.tc.core.Constants;

public class LearningCurveClusteringExample extends Experiments_ImplBase implements Constants {

	public static void main(String[] args) throws Exception{
		//		runAsapBaselineExperiment("ASAP_LearningCurve_Example", 
		//				System.getenv("DKPRO_HOME")+"/datasets/asap/originalData/train_repaired.txt", 
		//				System.getenv("DKPRO_HOME")+"/datasets/asap/originalData/test_public.txt", 
		//				"en", 
		//				1);
		runPowergradingBaselineExperiment("PG_LearningCurve_Example", 
				System.getenv("DKPRO_HOME")+"/datasets/powergrading//train_70.txt", 
				System.getenv("DKPRO_HOME")+"/datasets/powergrading//test_30.txt", 
				"en", 
				1);
	}



	protected static void runAsapBaselineExperiment(String experimentName, String trainData, String testData,
			String languageCode, Integer... questionIds) throws Exception {
		for (int id : questionIds) {
			CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(Asap2Reader.class,
					Asap2Reader.PARAM_INPUT_FILE, trainData,
					Asap2Reader.PARAM_PROMPT_IDS, id);

			CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(Asap2Reader.class,
					Asap2Reader.PARAM_INPUT_FILE, testData, 
					Asap2Reader.PARAM_PROMPT_IDS, id);
			runLearningCurveExperiment(experimentName + "_" + id + "", readerTrain, readerTest, languageCode);
		}
	}


	protected static void runPowergradingBaselineExperiment(String experimentName, String trainData, String testData,
			String languageCode, Integer... questionIds) throws Exception {
		for (int id : questionIds) {
			System.out.println("Prompt: "+id);
			CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(PowerGradingReader.class,
					PowerGradingReader.PARAM_INPUT_FILE, trainData,
					PowerGradingReader.PARAM_PROMPT_IDS, id);

			CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(PowerGradingReader.class,
					PowerGradingReader.PARAM_INPUT_FILE, testData, 
					PowerGradingReader.PARAM_PROMPT_IDS, id);
			runLearningCurveExperiment(experimentName + "_" + id + "", readerTrain, readerTest, languageCode);
		}
	}



	@SuppressWarnings("unchecked")
	private static void runLearningCurveExperiment(String experimentName, 
			CollectionReaderDescription readerTrain, 
			CollectionReaderDescription readerTest, String languageCode)
					throws Exception
	{     
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);
		dimReaders.put(DIM_READER_TEST, readerTest);

		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
		Dimension<Map<String, Object>> learningsArgsDims = getWekaLearningCurveClassificationArgsDim();
		int[] NUMBER_OF_TRAINING_INSTANCES = new int[] {10,20,40,80,160,320};
		Dimension<List<String>> dimClusteringArgs = Dimension.create("clusteringArguments",
				Arrays.asList(new String[] { SimpleKMeans.class.getName() })
				); 
		
		ParameterSpace pSpace = new ParameterSpace(
				Dimension.createBundle("readers", dimReaders),
				dimClusteringArgs,
				learningDims,
				Dimension.create(DIM_FEATURE_MODE, FM_UNIT),
				Dimension.create("dimension_number_of_training_instances", NUMBER_OF_TRAINING_INSTANCES),
				FeatureSettings.getFeatureSetsDimBaseline(),
				learningsArgsDims
				);

		runClusterClassificationCentroids(pSpace, experimentName, languageCode);
	}








}