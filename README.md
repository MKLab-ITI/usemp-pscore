# usemp-pscore
Implementation of the USEMP Privacy Scoring framework.

In the following we present an implementation of the USEMP privacy scoring framework (PScore). Please note that the version of the implementation that can currently be found here is near complete and a first version of the complete implementation should appear very shortly. 

The USEMP privacy scoring framework organizes the privacy data of users in a semantic manner. In particular, it assigns the data in a set of 8 categories that we call privacy dimensions (demographics, psychological traits, sexual profile, political attitudes, religious beliefs, health factors and condition, location, consumer profile). Each privacy dimension has a number of privacy attributes (for instance, some attributes under demographics are the age, gender and nationality of the user) and each attribute can take a number of values. Effectively, this creates a hierarchy of dimensions, attributes and values, each of which is associated by the framework with a set of privacy scores. This includes, for instance, the sensitivity of some attribute, the confidence that some value is true for the user, an overall privacy score that expresses the overall privacy risk associated with the particular dimension, attribute or value. Most of the scores are automatically computed by the framework based on input produced by external inference / analysis modules. That is, external mining modules, that analyze the data of the user identify specific values that hold of the user and will call the appropriate functions of the framework. Eventually, the framework will update the related scores in the complete hierarchy. Please note that, the implementation of the framework is flexible, so that the addition of new privacy dimensions and variables as well as the addition of new inference / analysis mechanisms can be done quite easily.


For more details on the framework and how it works please see the following paper:

Georgios Petkos, Symeon Papadopoulos, Yiannis Kompatsiaris.
PScore: a framework for enhancing privacy awareness in online social networks.

The current version of the implementation is also live for demo purposes and can be found at <https://scoring-framework.herokuapp.com> . This framework was build in **Python 3.4** with **MongoDB** database. Effectively, the framework maintains the privacy scoring framework in a Mongo database. The framework is accessible via a REST API that allows the creation, viewing and editing of privacy data in the database. 


For ease of use we have prepared two API calls collection files for the postman 2 plugin available in the chrome browser store, that contain all the methods with the correct headers and parameters data set the correct way for quick testing and debugging. The **scoring-frameworkAPI.json.postman_collection** file contains the API calls for our heroku deployed demo version. The **local-scoring-frameworkAPI.json.postman_collection** file contains the same calls but is targeted for local call when running on your PC.

#How to run
Make sure you have **Python 3.4** or higher installed, by running the the command `python --version`.
Some required modules are:

- aniso8601==0.92
- Flask==0.10.1
- Flask-RESTful==0.3.2
- flask-restful-swagger
- gunicorn==19.3.0
- itsdangerous==0.24
- Jinja2==2.7.3
- MarkupSafe==0.23
- pymongo==2.8
- pytz==2015.2
- six==1.9.0
- virtualenv==12.1.1
- Werkzeug==0.10.4
- newrelic
- flask-cors

To run the script just execute the command `python scoringFramework.py` or `python3.4 scoringFramework.py` depending on your installation of python.


#Available API methods
The base URL for all API calls is <https://scoring-framework.herokuapp.com>

- We start at the `/user` level. Here we offer the the possibility to create and delete users with all their respective data. So only `CREATE` and `DELETE` methods are accepted together with the user ID.

- The API also supports **value objects** though the `/user/value` endpoint. Here we offer full `CRUD` capabilities for these **value objects**. In the case where the **user object** is missing when using the `CREATE` method, we create the hole **user object** with the new **value object** in it.

- We go even deeper with the `/user/value/support` endpoint by allowing access to the support objects of some value. The support object is one level below the values level in our hierarchy, it points to the data that support the value and effectively links the data of the user with the scoring framework. This endpoint effectively triggers scores computation and is called after data produced by the external inference / analysis mechanisms.

Extra endpoints:

- We provide the `user/list` only `GET` method, that returns all the user objects from the database.

- `user/value/sensitivity`, `user/attribute/sensitivity` and `user/dimension/sensitivity` only `POST` method, gives the posibility to set the **sensitivity** in any of the available levels of the **user object**. When the sensitivity is set on a level higher than the **value** level, the same **sensitivity** is set on the all the levels below it in a cascade fasion.


User Object structure:


		{
			"user_influence": null,
			"user_privacy_score": null,
			"user_visibility_actual_audience": null,
			"_id": null,
			"overall_personal_data_value": null,
			"user_visibility_overall": null,
			"user_id": null,
			"personal_data_value_per_item": null,
			"user_visibility_label": "",
			"dimensions": {
				"dimension_name": {
					"dimension_privacy_score": null,
					"dimension_visibility_overall": null,
					"dimension_level_of_control": null,
					"dimension_visibility_label": null,
					"dimension_visibility_actual_audience": null,
					"dimension_sensitivity": null,
					"dimension_attributes": {
						"attribute_name": {
							"attribute_level_of_control": null,
							"attribute_visibility_label": null,
							"attribute_sensitivity": null,
							"attribute_visibility_actual_audience": null,
							"attribute_visibility_overall": null,
							"attribute_privacy_score": null,
							"attribute_values": {
								"value_name": {
									"value_confidence": 0.8,
									"value_visibility_label": null,
									"value_sensitivity": 0.999,
									"value_visibility_actual_audience": null,
									"value_level_of_control": null,
									"value_visibility_overall": null,
									"value_privacy_score": null,
									"value_is_inferred": true,
									"value_support": [
										{
											"support_inference_mechanism": "1",
											"support_data_pointer_id": "id1",
											"support_level_of_control": 0.2,
											"support_data_pointer_type": "string2",
											"support_confidence": 0.7
										},
										{
											"support_inference_mechanism": "2",
											"support_data_pointer_id": "qwer",
											"support_level_of_control": 0.2,
											"support_data_pointer_type": "string2",
											"support_confidence": 0.8
										},
										{
											"support_inference_mechanism": "3",
											"support_data_pointer_id": "pppppp",
											"support_level_of_control": 0.2,
											"support_data_pointer_type": "string2",
											"support_confidence": 0.2
										}
									]
								}
							}
						}
					}
				}
			}
		}
