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

def sendText():
    url = BASE_URL + "/message/sendText"
    textContent = input("textContent: ")
    receivingUserId = input("receivingUserId: ")
    lastId = input("lastId: ")
    data = {
        "textContent": textContent,
        "receivingUserId": receivingUserId,
    }
    if not data == "":
        data["lastId"] = lastId
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def sendImage():
    url = BASE_URL + "/message/sendImage"
    imageFilePath = input("image file path: ")
    receivingUserId = input("receivingUserId: ")
    lastId = input("lastId: ")
    if not os.path.exists(imageFilePath):
        print("file does not exist.")
        return
    with open(imageFilePath, "rb") as file:
        fileBase64 = base64.b64encode(file.read()).decode("utf-8")
    data = {
        "imageUrl": fileBase64,
        "receivingUserId": receivingUserId,
    }
    if not data == "":
        data["lastId"] = lastId
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def lis():
    url = BASE_URL + "/message/list"
    lastId = input("lastId: ")
    params = { }
    if not lastId == "":
        params["lastId"] = lastId
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.get(url, params=params, headers=headers)
    return response

def main():
    args = get_args()
    action_dict = {
        "sendText": sendText,
        "sendImage": sendImage,
        "list": lis,
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
