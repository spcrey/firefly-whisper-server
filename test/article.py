import base64
import requests
import argparse

BASE_URL = "http://localhost:9000"
# BASE_URL = "http://120.26.13.9:9000"

def get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--action", type=str)
    args = parser.parse_args()
    return args

def lis():
    url = BASE_URL + "/article/list"
    pageNum = input("page num: ")
    pageSize = input("page size: ")
    params = {
        "pageNum": pageNum,
        "pageSize": pageSize,
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization if Authorization else None}
    response = requests.get(url, params=params, headers=headers)
    return response

def add():
    url = BASE_URL + "/article/add"
    content = input("content: ")
    imageUrls = []
    for i in range(9):
        imagePath = input(f"image_{i+1}: ")
        if imagePath == "":
            break
        else:
            with open(imagePath, "rb") as file:
                fileBase64 = base64.b64encode(file.read()).decode("utf-8")
            imageUrls.append(fileBase64)

    data = {
        "content": content, "imageUrls": imageUrls
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def delete():
    url = BASE_URL + "/article/delete"
    id = input("id: ")
    data = {
        "id": id,
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def like():
    url = BASE_URL + "/article/like"
    id = input("id: ")
    data = {
        "id": id,
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def unlike():
    url = BASE_URL + "/article/unlike"
    id = input("id: ")
    data = {
        "id": id,
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def comment():
    url = BASE_URL + "/article/comment"
    content = input("content: ")
    articleId = input("articleId: ")
    data = {
        "content": content,
        "articleId": articleId,
    }
    with open("Authorization.cookie", "r") as file:
        Authorization = file.read()
    headers = {"Authorization": Authorization}
    response = requests.post(url, json=data, headers=headers)
    return response

def listComments():
    url = BASE_URL + "/article/listComments"
    articleId = input("articleId: ")
    params = {
        "articleId": articleId,
    }
    response = requests.get(url, params=params)
    return response

def main():
    args = get_args()
    action_dict = {
        "list": lis,
        "add": add,
        "delete": delete,
        "like": like,
        "unlike": unlike,
        "comment": comment,
        "listComments": listComments,
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
