from pymongo import MongoClient
from bson.objectid import ObjectId
from flask import Flask, make_response
from flask.ext.restful import reqparse, abort, Api, Resource, fields
from flask_restful_swagger import swagger
from functools import reduce
import json
import datetime
import os
import sys
import newrelic.agent

try:
    from flask.ext.cors import CORS  # The typical way to import flask-cors
except ImportError:
    # Path hack allows examples to be run without installation.
    import os
    parentdir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    os.sys.path.insert(0, parentdir)

    from flask.ext.cors import CORS


newrelic.agent.initialize()


#connection with mongodb database
uriString = 'mongodb://heroku_app35712253:h1percje0a7qvfhd38aq2li39t@ds039311.mongolab.com:39311/heroku_app35712253?replicaSet=rs-ds039311'
client = MongoClient(uriString)
#client = MongoClient('localhost', 27017)
db = client.heroku_app35712253
#print(db)
users_collection = db.users
#print(users_collection)
#users_collection.remove()



#helper methods
def mongodb_object_id(object_id_string):
    if len(object_id_string) == 24:
        return object_id_string
    else:
        raise ValueError('{} is not a valid ObjectId, it must be a 24-character hex string.'.format(str(object_id_string)))

def handle_request_error(error, data):
    if error == 404:
        if data:
            abort(404, message="{}".format(str(data)), status=404, data=[])
        else:
            abort(404, message="", status=404, data=[])
    elif error == 403:
        abort(403, message="{}".format(str(data)), status=403, data=[])
    elif error == 409:
        abort(409, message="Conflict. Already exists. Try using Http method PUT if you want to update the data.", status=409, data=[])
    elif error == 500:
        abort(500, message="Internal Server Error", status=500, data=[])

def score_float_validator(data):
    if (float(data) >= 0.0) & (float(data) <= 1.0):
        return float(data)
    else:
        raise ValueError('Float value is not in the range [0.0, 1.0]')

def support_object_validator(data):
    for object in data:
        if len(object.keys()) != 5:
            raise ValueError('Missing value(s) in support object (5 required): {}'.format(object))
        elif not isinstance(object['support_data_pointer_id'], str):
            raise ValueError('support_data_pointer_id should be a string')
        elif not isinstance(object['support_data_pointer_type'], str):
            raise ValueError('support_data_pointer_type should be a string')
        elif not isinstance(object['support_inference_mechanism'], str):
            raise ValueError('support_inference_mechanism should be a string')
        elif not ((float(object['support_confidence']) >= 0.0) & (float(object['support_confidence']) <= 1.0)):
            raise ValueError('support_confidence should be a float value in the range [0.0, 1.0]')
        elif not ((float(object['support_level_of_control']) >= 0.0) & (float(object['support_level_of_control']) <= 1.0)):
            raise ValueError('support_level_of_control should be a float value in the range [0.0, 1.0]')
    return data

def path_validator(value):
    if not isinstance(value, str):
        raise ValueError('path should be a string.')
    path = value.split('.')
    if ('' in path) | (len(path) != 3):
        raise ValueError('path should have 3 parts.')
    else:
        return path

def path_validator_dimension(value):
    if not isinstance(value, str):
        raise ValueError('path should be a string.')
    path = value.split('.')
    if ('' in path) | (len(path) != 2):
        raise ValueError('path should have 2 parts.')
    else:
        return path

def user_object_for_path(path, dictionary):
    return {
                "user_id": path[0],
                "user_privacy_score": None,
                "user_visibility_overall": None,
                "user_visibility_label": "",
                "user_visibility_actual_audience": None,
                "overall_personal_data_value": None,
                "user_influence": None,
                "personal_data_value_per_item": None,
                "dimensions": dimension_object_for_path(path, dictionary)
            }

def dimension_object_for_path(path, dictionary):
    return {
        path[2]: {
            "dimension_privacy_score": None,
            "dimension_visibility_overall": None,
            "dimension_visibility_label": "",
            "dimension_visibility_actual_audience": None,
            "dimension_level_of_control": None,
            "dimension_sensitivity": None,
            "dimension_attributes": attribute_object_for_path(path, dictionary)
        }
    }

def attribute_object_for_path(path, dictionary):
    return {
        path[4]: {
            "attribute_privacy_score": None,
            "attribute_visibility_overall": None,
            "attribute_visibility_label": None,
            "attribute_visibility_actual_audience": None,
            "attribute_level_of_control": None,
            "attribute_sensitivity": None,
            "attribute_values": value_object_for_path(path, dictionary)
        }
    }

def value_object_for_path(path, dictionary):
    return {
        path[6]: dictionary
    }


def get_path_from_data(dictionary):

    if dictionary == None:
        return None

    path = dictionary['path']
    del dictionary['path']
    if 'value_name' in dictionary.keys():
        path.append(dictionary['value_name'])
        del dictionary['value_name']

    list_to_return = []
    for i in range(0, len(path)):
        if i == 0:
            list_to_return.append(path[i])
        elif i == 1:
            list_to_return.append('dimensions')
            list_to_return.append(path[i])
        elif i == 2:
            list_to_return.append('dimension_attributes')
            list_to_return.append(path[i])
        elif i == 3:
            list_to_return.append('attribute_values')
            list_to_return.append(path[i])
    return list_to_return


def get_from_dict(data_dict, map_list):
#    return reduce(lambda d, k: d[k], mapList, dataDict)
#    print(data_dict)
    data_to_return = data_dict
    for key in map_list:
        if key in data_to_return:
            data_to_return = data_to_return[key]
        else:
            data_to_return = None
            break

    return data_to_return

def set_in_dict(dataDict, mapList, value):
    get_from_dict(dataDict, mapList[:-1])[mapList[-1]] = value



#methods to comunicate with mongodb
#user level functions
def read(user_id_string, object_id_string):
    user = None
    if user_id_string != None:
        user = users_collection.find_one({'user_id': user_id_string})
    elif object_id_string != None:
        user = users_collection.find_one({'_id': ObjectId(object_id_string)})

    if not user:
        return (None, 404)
    else:
        return ({"message": "OK", "status": 200, "data": user}, None)


def delete(user_id_string, object_id_string):
    result = None
    if user_id_string != None:
        result = users_collection.remove({'user_id': user_id_string})
    elif object_id_string != None:
        result = users_collection.remove({'_id': ObjectId(object_id_string)})

    if not result:
        return (None, 500)
    elif result["ok"] != 1:
        return (None, 500)
    elif result["n"] == 0:
        return (None, 404)
    else:
        return ({"message": "Deleted", "status": 200, "data": []}, None)


#value level functions
def read_value(dictionary):

    path_list = get_path_from_data(dictionary)

    user = read(path_list[0], None)[0]
    if user == None:
        return ("Can't find user {}.".format(str(path_list[0])), 404)
    else:
        user = user["data"]

    data_to_return = get_from_dict(user, path_list[1:])

    if data_to_return == None:
        return (None, 404)
    else:
        return ({"message": "OK", "status": 200, "data": {path_list[-1]: data_to_return}}, None)


def delete_value(dictionary):

    path_list = get_path_from_data(dictionary)

    user = read(path_list[0], None)[0]
    if user == None:
        return ("Can't find user {}.".format(str(path_list[0])), 404)
    else:
        user = user["data"]

    data_to_delete = get_from_dict(user, path_list[1:-1])
    if path_list[-1] in data_to_delete:
        del data_to_delete[path_list[-1]]
        users_collection.save(user)
        return ({"message": "Deleted value {}".format(str(path_list[-1])), "status": 200, "data": []}, None)
    else:
        return (None, 404)


def create_value(dictionary):

    path_list = get_path_from_data(dictionary)

    object_id = None

    user = read(path_list[0], None)[0]
    if user == None:
        object_id = users_collection.insert(user_object_for_path(path_list, dictionary))
        user_object = read(None, object_id)[0]["data"]
        calculate_score(user_object, path_list)
        object_id = users_collection.save(user_object)
        user_object = read(None, object_id)[0]["data"]
        data_to_return = get_from_dict(user_object, path_list[1:])
        if data_to_return == None:
            return (None, 500)
        else:
            return ({"message": "Created", "status": 200, "data":{path_list[-1]: data_to_return}}, None)
    else:
        user = user["data"]

    offset = -1
    for i in range(len(path_list), 0, -1):
        if get_from_dict(user, path_list[1:i]) != None:
            offset = i
            break
#    print(offset)
#    print(path_list[1:offset])

    if offset == 7:
        return (None, 409)
    elif offset == 6:
        get_from_dict(user, path_list[1:offset]).update(value_object_for_path(path_list, dictionary))
    elif offset == 4:
        get_from_dict(user, path_list[1:offset]).update(attribute_object_for_path(path_list, dictionary))
    elif offset == 2:
        get_from_dict(user, path_list[1:offset]).update(dimension_object_for_path(path_list, dictionary))

    calculate_score(user, path_list)

    object_id = users_collection.save(user)

    user_object = read(None, object_id)[0]["data"]
    data_to_return = get_from_dict(user_object, path_list[1:])
    if data_to_return == None:
        return (None, 500)
    else:
        return ({"message": "Created", "status": 200, "data":{path_list[-1]: data_to_return}}, None)


def update_value(dictionary):

    path_list = get_path_from_data(dictionary)

    object_id = None
    user = read(path_list[0], None)[0]
    if user == None:
        object_id = users_collection.insert(user_object_for_path(path_list, dictionary))
        user_object = read(None, object_id)[0]["data"]
        calculate_score(user_object, path_list)
        object_id = users_collection.save(user_object)
        user_object = read(None, object_id)[0]["data"]
        data_to_return = get_from_dict(user_object, path_list[1:])
        if data_to_return == None:
            return (None, 500)
        else:
            return ({"message": "Updated", "status": 200, "data":{path_list[-1]: data_to_return}}, None)
    else:
        user = user["data"]

    offset = -1
    for i in range(len(path_list), 0, -1):
        if get_from_dict(user, path_list[1:i]) != None:
            offset = i
            break

    if offset == 7:
        return (None, 409)
    elif offset == 6:
        get_from_dict(user, path_list[1:offset]).update(value_object_for_path(path_list, dictionary))
    elif offset == 4:
        get_from_dict(user, path_list[1:offset]).update(attribute_object_for_path(path_list, dictionary))
    elif offset == 2:
        get_from_dict(user, path_list[1:offset]).update(dimension_object_for_path(path_list, dictionary))

    calculate_score(user, path_list)

    object_id = users_collection.save(user)

    user_object = read(None, object_id)[0]["data"]
    data_to_return = get_from_dict(user_object, path_list[1:])
    if data_to_return == None:
        return (None, 500)
    else:
        return ({"message": "Updated", "status": 200, "data":{path_list[-1]: data_to_return}}, None)


#support level functions
def read_value_support(dictionary):

    path_list = get_path_from_data(dictionary)
    path_list.append('value_support')

    user = read(path_list[0], None)[0]
    if user == None:
        return ("Can't find user {}.".format(str(path_list[0])), 404)
    else:
        user = user["data"]

    data_to_return = get_from_dict(user, path_list[1:])

    if data_to_return == None:
        return (None, 404)

    if dictionary['index'] != None:
        index = dictionary['index']
        if index < len(data_to_return):
            data_to_return = data_to_return[dictionary['index']]
        else:
            return ('Index out of bounds.', 404)

    if data_to_return == None:
        return (None, 404)
    else:
        return ({"message": "OK", "status": 200, "data":{path_list[-1]: data_to_return}}, None)


def delete_value_support(dictionary):

    path_list = get_path_from_data(dictionary)
    path_list.append('value_support')

    user = read(path_list[0], None)[0]
    if user == None:
        return ("Can't find user {}.".format(str(path_list[0])), 404)
    else:
        user = user["data"]

    data_to_delete = get_from_dict(user, path_list[1:-1])

    if data_to_delete == None:
        return (None, 404)

    if dictionary['index'] != None:
        index = dictionary['index']
        if index < len(data_to_delete[path_list[-1]]):
            data_to_delete[path_list[-1]].pop(index)
            users_collection.save(user)
            return ({"message": "Deleted", "status": 200, "data":[]}, None)
        else:
            return ('Index out of bounds.', 404)
    else:
        data_to_delete[path_list[-1]] = []
        users_collection.save(user)
        return ({"message": "Deleted", "status": 200, "data":[]}, None)


def create_value_support(dictionary):

    path_list = get_path_from_data(dictionary)
    path_list.append('value_support')

    object_id = None
    user = read(path_list[0], None)[0]
    if user == None:
        object_id = users_collection.insert(user_object_for_path(path_list, dictionary))
        user_object = read(None, object_id)[0]["data"]
        calculate_score(user_object, path_list)
        object_id = users_collection.save(user_object)
        user_object = read(None, object_id)[0]["data"]
        data_to_return = get_from_dict(user_object, path_list[1:])
        if data_to_return == None:
            return (None, 500)
        else:
            return ({"message": "Created", "status": 200, "data":{path_list[-1]: data_to_return}}, None)
    else:
        user = user["data"]

    offset = -1
    for i in range(len(path_list), 0, -1):
        if get_from_dict(user, path_list[1:i]) != None:
            offset = i
            break

    if offset == 6:
        get_from_dict(user, path_list[1:offset]).update(value_object_for_path(path_list, dictionary))
    elif offset == 4:
        get_from_dict(user, path_list[1:offset]).update(attribute_object_for_path(path_list, dictionary))
    elif offset == 2:
        get_from_dict(user, path_list[1:offset]).update(dimension_object_for_path(path_list, dictionary))

    supports = get_from_dict(user, path_list[1:])

    if len(supports) == 0:
        new_supports = dictionary['value_support']
        supports = new_supports
        get_from_dict(user, path_list[1:-1])[path_list[-1]] = supports
    else:
        new_supports = dictionary['value_support']
        supports_to_add = []

        for new_sup in new_supports:
            if not any(
                        (sup['support_data_pointer_id'] == new_sup['support_data_pointer_id'])
    #                    & (sup['support_level_of_control'] == new_sup['support_level_of_control'])
    #                    & (sup['support_data_pointer_type'] == new_sup['support_data_pointer_type'])
    #                    & (sup['support_confidence'] == new_sup['support_confidence'])
    #                    & (sup['support_inference_mechanism'] == new_sup['support_inference_mechanism'])
                        for sup in supports):
                        supports_to_add.append(new_sup)

        if len(supports_to_add) > 0:
            supports = supports_to_add + supports
            get_from_dict(user, path_list[1:-1])[path_list[-1]] = supports

    calculate_score(user, path_list)

    object_id = users_collection.save(user)

    user_object = read(None, object_id)[0]["data"]
    data_to_return = get_from_dict(user_object, path_list[1:])
    if data_to_return == None:
        return (None, 500)
    else:
        return ({"message": "Created", "status": 200, "data":{path_list[-1]: data_to_return}}, None)


def update_value_support(dictionary):

    path_list = get_path_from_data(dictionary)
    path_list.append('value_support')

    object_id = None
    user = read(path_list[0], None)[0]
    if user == None:
        object_id = users_collection.insert(user_object_for_path(path_list, dictionary))
        user_object = read(None, object_id)[0]["data"]
        calculate_score(user_object, path_list)
        object_id = users_collection.save(user_object)
        user_object = read(None, object_id)[0]["data"]
        data_to_return = get_from_dict(user_object, path_list[1:])
        if data_to_return == None:
            return (None, 500)
        else:
            return ({"message": "Updated", "status": 200, "data":{path_list[-1]: data_to_return}}, None)
    else:
        user = user["data"]

    offset = -1
    for i in range(len(path_list), 0, -1):
        if get_from_dict(user, path_list[1:i]) != None:
            offset = i
            break

    if offset == 6:
        get_from_dict(user, path_list[1:offset]).update(value_object_for_path(path_list, dictionary))
    elif offset == 4:
        get_from_dict(user, path_list[1:offset]).update(attribute_object_for_path(path_list, dictionary))
    elif offset == 2:
        get_from_dict(user, path_list[1:offset]).update(dimension_object_for_path(path_list, dictionary))

    supports = get_from_dict(user, path_list[1:])

    new_supports = dictionary['value_support']
    supports_to_remove = []

    for new_sup in new_supports:
        for sup in supports:
            if sup['support_data_pointer_id'] == new_sup['support_data_pointer_id']:
                supports_to_remove.append(sup)

    if len(supports_to_remove) > 0:
        for temp in supports_to_remove:
            supports.remove(temp)
        get_from_dict(user, path_list[1:-1])[path_list[-1]] = new_supports + supports

    calculate_score(user, path_list)

    object_id = users_collection.save(user)

    user_object = read(None, object_id)[0]["data"]
    data_to_return = get_from_dict(user_object, path_list[1:])
    if data_to_return == None:
        return (None, 500)
    else:
        return ({"message": "Updated", "status": 200, "data":{path_list[-1]: data_to_return}}, None)


#other functions
def list_users():
    return ({"message": "OK", "status": 200, "data":list(users_collection.find())}, None)


def set_sensitivity(dictionary):
    path_list = get_path_from_data(dictionary)

    user = read(path_list[0], None)[0]["data"]
    if user == None:
        return ('User {} not found.'.format(str(path_list[0])), 404)

    object_in_path = get_from_dict(user, path_list[1:])

    if len(path_list) == 7:
        object_in_path['value_sensitivity'] = dictionary['sensitivity']
    elif len(path_list) == 5:
        object_in_path['attribute_sensitivity'] = dictionary['sensitivity']
        for value_key in object_in_path['attribute_values'].keys():
            value = object_in_path['attribute_values'][value_key]
            value['value_sensitivity'] = dictionary['sensitivity']
    elif len(path_list) == 3:
        object_in_path['dimension_sensitivity'] = dictionary['sensitivity']
        for attribute_key in object_in_path['dimension_attributes'].keys():
            attribute = object_in_path['dimension_attributes'][attribute_key]
            attribute['attribute_sensitivity'] = dictionary['sensitivity']
            for value_key in attribute['attribute_values'].keys():
                value = attribute['attribute_values'][value_key]
                value['value_sensitivity'] = dictionary['sensitivity']

    object_id = users_collection.save(user)

    if object_id != None:
        return ({"message": "Sensitivity changed", "status": 200, "data":[]}, None)

    pass


def calculate_score(user, path):
    
    if user == None:
        return

    path_list = list(path)

    if path_list[-1] == 'value_support':
        path_list.pop()

    attribute = get_from_dict(user, path_list[1:-2])
#    print(path_list[-2:])
    value = get_from_dict(attribute, path_list[-2:])
    supports = value['value_support']

#---value_is_inferred calculation
    if supports != None:
        if len(supports) > 0:
            if any(sup['support_inference_mechanism'] == 'declared' for sup in supports):
                value['value_is_inferred'] = False
            else:
                value['value_is_inferred'] = True

#---value_confidence calculation
    if value['value_is_inferred'] == False:
        value['value_confidence'] = 1.0
    else:
        all_values = attribute['attribute_values']
        grouped_confidence = {}

        for temp_value_key in all_values.keys():
            temp_value = all_values[temp_value_key]
            for temp_support in temp_value['value_support']:
                key = (temp_support['support_inference_mechanism'], temp_support['support_data_pointer_id'])
                if temp_value['value_sensitivity'] == None:
                    sensitivity = 0
                else:
                    sensitivity = temp_value['value_sensitivity']
                val = (temp_value_key, sensitivity, temp_support['support_confidence'])
                if key in grouped_confidence:
                    grouped_confidence[key].append(val)
                else:
                    grouped_confidence[key] = [val]

#        keep only supports that are in all values
        keys_to_remove = []
        for key in grouped_confidence.keys():
            if len(grouped_confidence[key]) != len(all_values.keys()):
                keys_to_remove.append(key)

        for key in keys_to_remove:
            del grouped_confidence[key]

#        print(grouped_confidence)

#        calculate all confidences
        temp_confience = {}
#        if len(grouped_confidence.keys())
        for key in grouped_confidence.keys():
            temp_score = 0
            for tupel in grouped_confidence[key]:
                print(tupel)
                temp_score = temp_score + tupel[1] * tupel [2]
            temp_confience[key] = temp_score

#        print(temp_confience)

#        get max confidence
        confidence_key = None
        for key in temp_confience.keys():
            if confidence_key == None:
                confidence_key = key
            elif temp_confience[key] > temp_confience[confidence_key]:
                confidence_key = key

#        print(confidence_key)

        confidences = grouped_confidence[confidence_key]
#        print(confidences)

        for temp_tuple in confidences:
            all_values[temp_tuple[0]]['value_confidence'] = temp_tuple[2]

        pass

    get_from_dict(user, path_list[1:-1])[path_list[-1]] = value

    return user






#custom json encoder for mongo ids and datetime
class CustomEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, ObjectId):
            return str(obj)
        if isinstance(obj, datetime.datetime):
            return int(obj.strftime('%s'))
        return json.JSONEncoder.default(self, obj)

#function to use the custom json encoder
def custom_json_output(data, code, headers=None):
    dumped = json.dumps(data, cls=CustomEncoder)
    resp = make_response(dumped, code)
    resp.headers.extend(headers or {})
    return resp



#start of the API
app = Flask(__name__)
CORS(app, resources=r'/api/*', allow_headers='Content-Type')
api = swagger.docs(Api(app), apiVersion='1.0', produces=["application/json"], basePath='https://scoring-framework.herokuapp.com/', description='USEMP scoring-framework API')
#api = Api(app)
api.representations.update({'application/json': custom_json_output})



class UsersList(Resource):
    "My TODO API"
    @swagger.operation(
        notes = 'notes ---',
#        responseClass = UserItem.__name__,
        summary = 'summary ---',
        responseMessages=[
            {
                "code": 201,
                "message": "Created. The URL of the created blueprint should " + "be in the Location header"
            },
            {
                "code": 405,
                "message": "Invalid input"
            }
        ])
    def get(self):
        results = list_users()
        return results[0], 200, {'Access-Control-Allow-Origin': '*'}

    def options (self, **args):
        return {'Allow' : 'GET' }, 200, {'Access-Control-Allow-Origin': '*', 'Access-Control-Allow-Methods': 'GET', 'Access-Control-Allow-Headers': 'Content-Type'}

api.add_resource(UsersList, '/users/list', endpoint = 'list')


parser_user_value_sensitivity = reqparse.RequestParser()
parser_user_value_sensitivity.add_argument('path', type=path_validator, required=True, location='json', help="This value should be a dot separated string path to the location of the new value that is being added in the format user_id.dimension_name.attribute_name (Ex: 12346345.demographics.age)")
parser_user_value_sensitivity.add_argument('value_name', type=str, required=True, location='json', help="This is the name of the value that is represented by this record.")
parser_user_value_sensitivity.add_argument('sensitivity', type=score_float_validator, required=True, location='json', help="This is the sensitivity score for this particular value. It is either provided by the user or is computed from prior knowledge.")

class userValueSensitivity(Resource):

    def post(self):
        args = parser_user_value_sensitivity.parse_args()
        return_object = set_sensitivity(args)
        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def put(self):
        args = parser_user_value_sensitivity.parse_args()
        return_object = set_sensitivity(args)
        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

api.add_resource(userValueSensitivity, '/user/value/sensitivity', endpoint = 'value/sensitivity')



parser_user_attribute_sensitivity = reqparse.RequestParser()
parser_user_attribute_sensitivity.add_argument('path', type=path_validator, required=True, location='json', help="This value should be a dot separated string path to the location of the new value that is being added in the format user_id.dimension_name.attribute_name (Ex: 12346345.demographics.age)")
parser_user_attribute_sensitivity.add_argument('sensitivity', type=score_float_validator, required=True, location='json', help="This is the sensitivity score for this particular value. It is either provided by the user or is computed from prior knowledge.")

class userAttributeSensitivity(Resource):

    def post(self):
        args = parser_user_attribute_sensitivity.parse_args()
        return_object = set_sensitivity(args)
        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def put(self):
        args = parser_user_value_sensitivity.parse_args()
        return_object = set_sensitivity(args)
        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

api.add_resource(userAttributeSensitivity, '/user/attribute/sensitivity', endpoint = 'attribute/sensitivity')



parser_user_dimension_sensitivity = reqparse.RequestParser()
parser_user_dimension_sensitivity.add_argument('path', type=path_validator_dimension, required=True, location='json', help="This value should be a dot separated string path to the location of the new value that is being added in the format user_id.dimension_name.attribute_name (Ex: 12346345.demographics.age)")
parser_user_dimension_sensitivity.add_argument('sensitivity', type=score_float_validator, required=True, location='json', help="This is the sensitivity score for this particular value. It is either provided by the user or is computed from prior knowledge.")

class userDimensionSensitivity(Resource):

    def post(self):
        args = parser_user_dimension_sensitivity.parse_args()
        return_object = set_sensitivity(args)
        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def put(self):
        args = parser_user_value_sensitivity.parse_args()
        return_object = set_sensitivity(args)
        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

api.add_resource(userDimensionSensitivity, '/user/dimension/sensitivity', endpoint = 'dimension/sensitivity')



class Ping(Resource):

    def get(self):
        return {"status": 200}

api.add_resource(Ping, '/ping', endpoint = 'ping')



class ClearAll(Resource):

    def get(self):
        users_collection.remove()
        return {"status": 200}

api.add_resource(ClearAll, '/clear_all', endpoint = 'clear_all')



parser_user = reqparse.RequestParser()
parser_user.add_argument('_id',       type=mongodb_object_id,   required=False, location='args',    help="The object_id in the database. This is a mongodb id, it must be a 24-character hex string.")
parser_user.add_argument('user_id',   type=str,   required=False, location='args',    help="The user_id in the database.")



class User(Resource):

    def get(self):
        args = parser_user.parse_args()

        return_object = None
        if (args['user_id'] == None) & (args['_id'] == None):
            handle_request_error(403, 'Can\'t find object without ?user_id=... or ?_id=...')
        elif (args['user_id'] != None) & (args['_id'] != None):
            handle_request_error(403, 'Please don\'t use both of the arguments.')
        elif args['user_id'] != None:
            return_object = read(args['user_id'], None)
        elif args['_id'] != None:
            return_object = read(None, args['_id'])

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def delete(self):
        args = parser_user.parse_args()

        return_object = ()
        if (args['user_id'] == None) & (args['_id'] == None):
            handle_request_error(403, 'Can\'t find object without ?user_id=... or ?_id=...')
        elif (args['user_id'] != None) & (args['_id'] != None):
            handle_request_error(403, 'Please don\'t use both of the arguments.')
        elif args['user_id'] != None:
            return_object = delete(args['user_id'], None)
        elif args['_id'] != None:
            return_object = delete(None, args['_id'])

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

api.add_resource(User, '/user', endpoint = 'user')



parser_user_value = reqparse.RequestParser()
parser_user_value.add_argument('path',                  type=path_validator,            required=True,  location='json',    help="This value should be a dot separated string path to the location of the new value that is being added in the format user_id.dimension_name.attribute_name (Ex: 12346345.demographics.age)")
parser_user_value.add_argument('value_name',            type=str,                       required=True,  location='json',    help="This is the name of the value that is represented by this record.")
parser_user_value.add_argument('value_confidence',                 type=score_float_validator,     required=False,  location='json',    help="This is the aggregated confidence score for the specified value.")
parser_user_value.add_argument('value_sensitivity',                type=score_float_validator,     required=False, location='json',    help="This is the sensitivity score for this particular value. It is either provided by the user or is computed from prior knowledge.")
parser_user_value.add_argument('value_visibility_overall',         type=score_float_validator,     required=False,  location='json',    help="This is the overall visibility score for this particular value. It will depend on the user's privacy settings on the content that supports the value.")
parser_user_value.add_argument('value_visibility_label',           type=str,                       required=False,  location='json',    help="This is qualitative visibility label that represents the widest group of audience to which information about this value is accessible.")
parser_user_value.add_argument('value_visibility_actual_audience', type=int,                       required=False,  location='json',    help="This is an estimate of the actual audience that has access to information related to this value.")
parser_user_value.add_argument('value_is_inferred',                type=bool,                      required=False,  location='json',    help="This is a binary field that defines if the value has been declared by the user or has been inferred.")
parser_user_value.add_argument('value_level_of_control',           type=score_float_validator,     required=False,  location='json',    help="This represents the ability of the user to control the disclosure of information related to this value.")
parser_user_value.add_argument('value_privacy_score',              type=score_float_validator,     required=False,  location='json',    help="This is the overall privacy score for that particular value, which is a function of the sensitivity, (aggregated) confidence and (aggregated) visibility scores")
parser_user_value.add_argument('value_support',                    type=support_object_validator,  required=False,  location='json',    help="This is an array that includes support records. Support records can only appear as parts of this array.")

#parser_user_value.add_argument('value_sensitivity',     type=score_float_validator,     required=True,  location='json',    help="This is the sensitivity score for this particular value. It is either provided by the user or is computed from prior knowledge.")
#parser_user_value.add_argument('value_support',         type=support_object_validator,  required=True,  location='json',    help="This is an array that includes support records. Support records can only appear as parts of this array.")

parser_user_value_simple = reqparse.RequestParser()
parser_user_value_simple.add_argument('path', type=path_validator, required=True, location='args', help="This value should be a dot separated string path to the location of the new value that is being added in the format user_id.dimension_name.attribute_name (Ex: 12346345.demographics.age)")
parser_user_value_simple.add_argument('value_name', type=str, required=True, location='args', help="This is the name of the value that is represented by this record.")

class UserValue(Resource):

    def get(self):
        args = parser_user_value_simple.parse_args()
        return_object = read_value(args)

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def delete(self):
        args = parser_user_value_simple.parse_args()
        return_object = delete_value(args)

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def post(self):
        args = parser_user_value.parse_args()
        return_object = create_value(args)

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], None)
        pass

    def put(self):
        parser_user_value_put = parser_user_value.copy()
        parser_user_value_put.replace_argument('value_support', type=support_object_validator, required=False, location='json', help="This is an array that includes support records. Support records can only appear as parts of this array.")
        parser_user_value_put.replace_argument('value_sensitivity', type=score_float_validator, required=False, location='json', help="This is the sensitivity score for this particular value. It is either provided by the user or is computed from prior knowledge.")
        args = parser_user_value_put.parse_args()
        return_object = update_value(args)
        if not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], None)
        pass

api.add_resource(UserValue, '/user/value', endpoint = 'value')



class UserValueSupport(Resource):

    def get(self):
        parser_get = parser_user_value_simple.copy()
        parser_get.add_argument('index', type=int, required=False, choices=range(0, sys.maxsize), location='args', help="The index of the support object in the value_support array. Integer.")
        args = parser_get.parse_args()
        return_object = read_value_support(args)

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def delete(self):
        parser_delete = parser_user_value_simple.copy()
        parser_delete.add_argument('index', type=int, required=False, choices=range(0, sys.maxsize), location='args', help="The index of the support object in the value_support array. Integer.")
        args = parser_delete.parse_args()
        return_object = delete_value_support(args)

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def post(self):
        parser_post = parser_user_value.copy()
        parser_post.replace_argument('value_sensitivity', type=score_float_validator, required=False, location='json', help="This is the sensitivity score for this particular value. It is either provided by the user or is computed from prior knowledge.")
#        parser_post.replace_argument('path', type=path_validator, required=True, location='json', help="This value should be a dot separated string path to the location of the new value that is being added in the format user_id.dimension_name.attribute_name (Ex: 12346345.demographics.age)")
#        parser_post.replace_argument('value_name', type=str, required=True, location='json', help="This is the name of the value that is represented by this record.")
#        parser_post.add_argument('value_support', type=support_object_validator, required=True, location='json', help="This is an array that includes support records. Support records can only appear as parts of this array.")
        args = parser_post.parse_args()
        return_object = create_value_support(args)

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

    def put(self):
        parser_put = parser_user_value_simple.copy()
        parser_put.replace_argument('path', type=path_validator, required=True, location='json', help="This value should be a dot separated string path to the location of the new value that is being added in the format user_id.dimension_name.attribute_name (Ex: 12346345.demographics.age)")
        parser_put.replace_argument('value_name', type=str, required=True, location='json', help="This is the name of the value that is represented by this record.")
        parser_put.add_argument('value_support', type=support_object_validator, required=True, location='json', help="This is an array that includes support records. Support records can only appear as parts of this array.")
        args = parser_put.parse_args()
        return_object = update_value_support(args)

        if not return_object:
            handle_request_error(500, None)
        elif not return_object[1]:
            return return_object[0]
        else:
            handle_request_error(return_object[1], return_object[0])
        pass

api.add_resource(UserValueSupport, '/user/value/support', endpoint = 'support')



#run API
if __name__ == "__main__":
#    app.run()
    app.run(debug=True)
