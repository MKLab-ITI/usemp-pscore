# usemp-pscore
Implementation of the USEMP Privacy Scoring framework and inference modules.

This package contains:
- An implementation of the USEMP privacy scoring framework (PScore). 
- An implementation of four inference modules that work on social network data and predict a number of personal attributes.
- Code for fetching the data of a Facebook user and saving it to a number of files.
- A full example in which the data of a user is fetched from Facebook, it is passed through the inference modules, the inference modules feed their results in the disclosure scoring framework and eventually the disclosure scores of the user are shown through a web-page visualization.

The USEMP privacy scoring framework organizes the privacy data of users in a semantic manner. In particular, it assigns the data in a set of 8 categories that we call privacy dimensions (demographics, psychological traits, sexual profile, political attitudes, religious beliefs, health factors and condition, location, consumer profile). Each privacy dimension has a number of privacy attributes (for instance, some attributes under demographics are the age, gender and nationality of the user) and each attribute can take a number of values. Effectively, this creates a hierarchy of dimensions, attributes and values, each of which is associated by the framework with a set of privacy scores. This includes, for instance, the sensitivity of some attribute, the confidence that some value is true for the user, an overall privacy score that expresses the overall privacy risk associated with the particular dimension, attribute or value. Most of the scores are automatically computed by the framework based on input produced by external inference / analysis modules. That is, external mining modules, that analyze the data of the user identify specific values that hold of the user and will call the appropriate functions of the framework. Eventually, the framework will update the related scores in the complete hierarchy. Please note that, the implementation of the framework is flexible, so that the addition of new privacy dimensions and variables as well as the addition of new inference / analysis mechanisms can be done quite easily.


For more details on the framework and how it works please see the following paper:

Georgios Petkos, Symeon Papadopoulos, Yiannis Kompatsiaris.
PScore: a framework for enhancing privacy awareness in online social networks.


#How to run
First, please download the following zip file that contains a number of model files and various static data that are used by the code:
https://www.dropbox.com/s/jz3730djl9gtr5w/pscore-resources.zip?dl=0
Please unzip it in the /src/main/resources folder of the project. Unfortunately, the size of the files in the archive did not allow us to put them to github. Apologies for the inconvenience.


#Explanation of inference modules 



#Explanation of disclosure scoring framework



#Feeding the results of new inference modules to the disclosure scoring framework



