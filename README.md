# usemp-pscore
Implementation of the USEMP disclosure scoring framework and inference modules.

This package Java code for:
- The USEMP disclosure scoring framework (PScore).  
- Four inference modules that work on social network data and predict a number of personal attributes.
- Fetching the data of a Facebook user and saving it to a number of files.
- A full example in which the data of a user is fetched from Facebook and passed through the inference modules. The inference modules feed the results in the disclosure scoring framework and eventually the disclosure scores of the user are shown through a web-page visualization.

# Overview of the disclosure scoring framework

The USEMP privacy scoring framework attempts to quantify the exposure of different aspects of a person's information through a social network. In order to facilitate the presentation of data, it organizes the different attributes of users in a number of categories that we call privacy dimensions (demographics, psychology, sexuality, politics, religion, health, hobbies,employmentm relationships). For instance, some attributes under demographics are the age, gender and nationality of the user. Also, each attribute can take a number of values; for instance the attribute gender can take the values male and female. Effectively, this creates a hierarchy of dimensions, attributes and values: each dimension has a set of attributes and each attribute can take a number of values. Now, the exposure of each dimension attribute or value is associated by the framework with a set of privacy scores. This includes, for instance, the sensitivity of some attribute, the confidence that some value is true for the user, an overall disclosure score that expresses the overall risk associated with the particular dimension, attribute or value. These scores are computed by the framework by taking into account the results produced by a number of inference modules that analyze the social network data of the user and produce estimates about the values of the different attributes.

The implementation of the disclosure scoring framework can be found in the package usemp.certh.scoring

For more details on the framework and how it works please see the following paper:

Georgios Petkos, Symeon Papadopoulos, Yiannis Kompatsiaris.
PScore: a framework for enhancing privacy awareness in online social networks.

#Overview of inference modules 

Apart from the code for the USEMP disclosure scoring framework, this package also contains the implementations of a number of inference modules that analyze the social network data of users and produce estimates about the values of the users' attributes. Four inference modules are included in this package:

- Pre-pilot classifier. 
	The implementation of this module can be found in the package usemp.certh.inference.preprepilot . This module works on the collection of all Facebook data of a user and looks for associations that may not be that obvious. In particular, it takes into account the set of likes, posts and visual concepts (ImageNet concepts) detected in the images posted by the user. Please note that this package does not contain code for visual concept detection. The provided implementation can work without visual concepts, but if visual concepts are available in the form of the sample xml file, then the pre-pilot classifier will take them into account. Internally, the classifier utilizes statistical models that have been trained with data collected through a user study. For more details on these models please see the following paper:
	Eleftherios Spyromitros-Xioufis, Georgios Petkos, Symeon Papadopoulos, Rob Heyman, Yiannis Kompatsiaris. Perceived versus Actual Predictability of Personal Information in Social Networks. Internet Science 2016.
- Likes classifier.
	The implementation of this module can be found in the package usemp.certh.inference.likesclassifier . This module works only on the likes of users. For instance, if a user has liked the Starbucks page, then this module will associate this to the attribute "coffee", under the "demographics" dimension.
- URLs classifier.
	The implementation of this module can be found in the package usemp.certh.inference.urlclassifier . This module works on the URLs posted by the users. For instance, if a user has posted a URL at imdb, then this module will associate this URL to the attribute "series movies" under the "hobbies" dimension.
- Visual concepts mapper.
	The implementation of this module can be found in the package usemp.certh.inference.visualconceptsclassifier . This module works on the visual concepts detected in the images posted by the user. For instance, if the visual concept "beer" is detected in the images of the user, then this image is associated to the attribute "alcohol" under the "health" dimension. Please note though that, as mentioned before, a visual concept detector is not provided with this package and therefore, this module will only work if a concepts file is provided in the right format, using some other source.

Please also note that, the implementation of the framework is flexible, so that the addition of new privacy dimensions and variables as well as the addition of new inference / analysis mechanisms can be done quite easily.

#How to run
First, please download the following zip file that contains a number of model files and various static data files that are used by the code:
https://www.dropbox.com/s/jz3730djl9gtr5w/pscore-resources.zip?dl=0
Please unzip it in the /src/main/resources folder of the project. Unfortunately, the size of the files in the archive did not allow us to put them to github. Apologies for the inconvenience.

Please note that code for fetching a user's data from Facebook and storing it to files is also provided. This can be found in the package usemp.certh.facebookpersonaldatafetcher . This is used in order for a user to be able to test the framework and inference modules with their own data. What is required is just a valid Facebook access token that can be obtained from the Graph API explorer:
https://developers.facebook.com/tools/explorer/

An example of how the different parts of the code can be used are provided in the class usemp.certh.main.Main . This does the following:
- Fetches the data of the user from Facebook using the FacebookPersonalDataFetcher class. Please note that the Facebook access token and directory to save the data are first defined and then passed to the class.
- Initializes an object of type UserDataAccessFromFile that loads the user's data from the files and passes it on request to the different modules that analyze it. 
- Initializes the classes for the four inference modules.
- Executes the inference modules with data fed into them from the UserDataAccessFromFile object. 
- Saves the scores of the user in the respective file.
- Copies into the user's data directory a web page that visualizes the user's disclosure scores.

Please note that after execution the data directory will have the following contents:
- myAlbums.json : This file contains details about the user's photo albums.
- myDetails.json : Some basic information about the user's Facebook profile.
- myFriends.json : The set of the user's Facebook friends.
- myLikes.json : The user's liked pages.
- myPhotos.json : The details of the user's photos.
- myPosts.json : The posts of the user.
- myScores.json : The disclosure scores of the user in json format.
- myStatuses.json : The status updates posted by the user.
- concepts.xml : This xml files contains the visual concepts (ImageNet concepts) detected in the images of the user. As mentioned, this is not produced by the provided code. If it is available it will be taken into account by the code, otherwise results are still produced. For a sample file please see TODO...
- photos: This directory contains the images of the user.
- visualization : This directory contains the visualization of the user's scores. Please open the file USEMP.htm that can be found inside it.

A sample directory with mock data can be found here https://www.dropbox.com/s/7tqyxr56ss8shr3/myFacebookDataTest.zip?dl=0 .

