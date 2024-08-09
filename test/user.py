import base64
import os
import requests

BASE_URL = "http://localhost:9000"
BASE_URL = "http://120.26.13.9:9000"

import argparse

def get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--action", type=str)
    args = parser.parse_args()
    return args

def get_authorization():
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    return Authorization

def register():
    phoneNumber = input("phone number: ")
    password = input("password: ")
    re_password = input("second password: ")
    data = {
        "phoneNumber": phoneNumber,
        "password": password,
        "rePassword": re_password,
    }
    url = BASE_URL + "/user/register"
    response = requests.post(url, json=data)
    return response

def sendSms():
    url = BASE_URL + "/user/sendSms"
    phoneNumber = input("phone number: ")
    data = {
        "phoneNumber": phoneNumber,
    }
    response = requests.post(url, json=data)
    return response

def loginByCode():
    url = BASE_URL + "/user/loginByCode"
    phoneNumber = input("phone number: ")
    code = input("code: ")
    data = {
        "phoneNumber": phoneNumber,
        "code": code,
    }
    response = requests.post(url, json=data)
    Authorization = response.json()["data"]
    if Authorization:
        with open("Authorization.cookie", "w") as file:
            file.write(Authorization)
    return response

def loginByPassword():
    url = BASE_URL + "/user/loginByPassword"
    phoneNumber = input("phone number: ")
    password = input("password: ")
    data = {
        "phoneNumber": phoneNumber,
        "password": password,
    }
    response = requests.post(url, json=data)
    Authorization = response.json()["data"]
    if Authorization:
        with open("Authorization.cookie", "w") as file:
            file.write(Authorization)
    return response

def info():
    url = BASE_URL + "/user/info"
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.get(url, headers=headers)
    return response

def infoOther():
    url = BASE_URL + "/user/infoOther"
    userId = input("userId: ")
    params = {
        "userId": userId,
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization if Authorization else None}
    response = requests.get(url, params=params, headers=headers)
    return response

def update():
    url = BASE_URL + "/user/update"
    nickname = input("nickname: ")
    email = input("email: ")
    personalSignature = input("personal signature: ")
    data = {}
    if not nickname == "":
        data["nickname"] = nickname
    if not email == "":
        data["email"] = email
    if not personalSignature == "":
        data["personalSignature"] = personalSignature
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def updateAvatar():
    url = BASE_URL + "/user/updateAvatar"
    avatarFilePath = input("avatar file path: ")
    if not os.path.exists(avatarFilePath):
        print("file does not exist.")
        return
    
    with open(avatarFilePath, "rb") as file:
        fileBase64 = base64.b64encode(file.read()).decode("utf-8")
    data = {
        "avatarUrl": fileBase64
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {
        "Authorization": Authorization,
    }
    response = requests.post(url, json=data, headers=headers)
    return response

def updatePassword():
    url = BASE_URL + "/user/updatePassword"
    phoneNumber = input("phone number: ")
    password = input("password: ")
    code = input("code: ")
    data = {
       "phoneNumber": phoneNumber,
       "password": password,
       "code": code,
    }
    response = requests.post(url, json=data)
    return response

def listFolloweds():
    url = BASE_URL + "/user/listFolloweds"
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.get(url, headers=headers)
    return response

def listFollowers():
    url = BASE_URL + "/user/listFollowers"
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.get(url, headers=headers)
    return response

def follow():
    url = BASE_URL + "/user/follow"
    followedUserId = input("followed user id: ")
    data = {
       "followedUserId": followedUserId,
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def unfollow():
    url = BASE_URL + "/user/unfollow"
    followedUserId = input("followed user id: ")
    data = {
       "followedUserId": followedUserId,
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def logout():
    url = BASE_URL + "/user/logout"
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, headers=headers)
    with open("Authorization.cookie", "w") as file:
            file.write("")
    return response

def main():
    args = get_args()
    action_dict = {
        "register": register,
        "sendSms": sendSms,
        "loginByCode": loginByCode,
        "loginByPassword": loginByPassword,
        "info": info,
        "infoOther": infoOther,
        "update": update,
        "updatePassword": updatePassword,
        "updateAvatar": updateAvatar,
        "listFolloweds": listFolloweds,
        "listFollowers": listFollowers,
        "follow": follow,
        "unfollow": unfollow,
        "logout": logout,
    }
    response = action_dict[args.action]
    result = response()
    if not result == None:
        print(f"status: {result.status_code}")
        text = result.text
        if text=="":
            print("text: None")
        else:
            print(f"text: {text}")

if __name__ == "__main__":
    main()
