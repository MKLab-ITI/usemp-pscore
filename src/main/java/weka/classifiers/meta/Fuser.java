package weka.classifiers.meta;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class Fuser extends SingleClassifierEnhancer {

	private String featureBandPrefixes;
	private Classifier baseClassifier;
	private String fusionType;

	public FilteredClassifier[] perFeatureBandModels;
	public FilteredClassifier globalModel;

	/**
	 * 
	 * @param bandPrefixes
	 *            a comma separated list of the feature bands
	 * @param baseClassifier
	 *            the classifiers to use
	 * @param fusionType
	 *            the fusion type, currently only "late" and "early" are supported ("early" has efficiency
	 *            issues)
	 */
	public Fuser(String bandPrefixes, Classifier baseClassifier, String fusionType) {
		this.featureBandPrefixes = bandPrefixes;
		this.baseClassifier = baseClassifier;
		this.fusionType = fusionType;
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {
		// create a FilteredClassifier for each feature band
		String[] prefixes = featureBandPrefixes.split(",");

		if (fusionType.equals("late")) {
			perFeatureBandModels = new FilteredClassifier[prefixes.length];

			for (int i = 0; i < prefixes.length; i++) {
				// TODO check if the use of StringBuffer speeds up the classifier!
				StringBuffer sb = new StringBuffer();
				// find attribute ranges
				for (int j = 0; j < data.numAttributes(); j++) {
					if (data.attribute(j).name().startsWith(prefixes[i] + "_")) {
						sb.append((j + 1) + ",");
					}
				}
				if (sb.length() == 0) {
					throw new Exception("0 attributes with this prefix found!");
				}
				// add the class index as well!
				sb.append((data.classIndex() + 1));

				Remove rem = new Remove();
				rem.setAttributeIndices(sb.toString());
				rem.setInvertSelection(true);
				rem.setInputFormat(data);

				perFeatureBandModels[i] = new FilteredClassifier();
				perFeatureBandModels[i].setFilter(rem);
				perFeatureBandModels[i].setClassifier(AbstractClassifier.makeCopy(baseClassifier));

				perFeatureBandModels[i].buildClassifier(data);
			}
		} else if (fusionType.equals("early")) {
			globalModel = new FilteredClassifier();

			String attributeIndices = "";
			for (int i = 0; i < prefixes.length; i++) {
				// find attribute ranges
				for (int j = 0; j < data.numAttributes(); j++) {
					if (data.attribute(j).name().startsWith(prefixes[i] + "_")) {
						attributeIndices += (j + 1) + ",";
					}
				}
				if (attributeIndices.length() == 0) {
					throw new Exception("0 attributes with this prefix found!");
				}
			}
			// add the class index as well!
			attributeIndices += (data.classIndex() + 1);

			Remove rem = new Remove();
			rem.setAttributeIndices(attributeIndices);
			rem.setInvertSelection(true);
			rem.setInputFormat(data);

			globalModel = new FilteredClassifier();
			globalModel.setFilter(rem);
			globalModel.setClassifier(AbstractClassifier.makeCopy(baseClassifier));

			globalModel.buildClassifier(data);
		} else {
			throw new Exception("Unknown fusion!");
		}

	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {

		double[] dist = new double[instance.numClasses()];
		int type = instance.classAttribute().type();
		if (type == Attribute.NOMINAL) {
			if (fusionType.equals("late")) {
				for (int i = 0; i < perFeatureBandModels.length; i++) {
					double[] featyreBandDist = perFeatureBandModels[i].distributionForInstance(instance);
					for (int j = 0; j < dist.length; j++) {
						dist[j] += featyreBandDist[j];
					}
				}
				for (int j = 0; j < dist.length; j++) {
					dist[j] /= perFeatureBandModels.length;
				}
			} else if (fusionType.equals("early")) {
				dist = globalModel.distributionForInstance(instance);
			}
		} else if (type == Attribute.NUMERIC) {// the same code as for NOMINAL is used at the moment
			if (fusionType.equals("late")) {
				for (int i = 0; i < perFeatureBandModels.length; i++) {
					double[] featyreBandDist = perFeatureBandModels[i].distributionForInstance(instance);
					for (int j = 0; j < dist.length; j++) {
						dist[j] += featyreBandDist[j];
					}
				}
				for (int j = 0; j < dist.length; j++) {
					dist[j] /= perFeatureBandModels.length;
				}
			} else if (fusionType.equals("early")) {
				dist = globalModel.distributionForInstance(instance);
			}
		} else {
			throw new Exception("Wrong target attribute type!");
		}
		return dist;

	}

}
