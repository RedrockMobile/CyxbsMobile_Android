# coding=UTF-8
import json
from urllib import request
import ssl
import sys
import bs4


ssl._create_default_https_context = ssl._create_unverified_context

APP_VERIFICATION_TOKEN = "0.11"


def get_tenant_access_token():
    url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal/"
    headers = {
        "Content-Type": "application/json"
    }
    req_body = {
        "app_id": APP_ID,
        "app_secret": APP_SECRET
    }
    data = bytes(json.dumps(req_body), encoding='utf8')
    req = request.Request(url=url, data=data, headers=headers, method='POST')
    try:
        response = request.urlopen(req)
    except Exception as e:
        print(e.read().decode())
        return ""

    rsp_body = response.read().decode('utf-8')
    rsp_dict = json.loads(rsp_body)
    code = rsp_dict.get("code", -1)
    if code != 0:
        print("get tenant_access_token error, code =", code, rsp_dict)
        return ""
    return rsp_dict.get("tenant_access_token", "")


def send_message(token, chat_id, content_):
    url = "https://open.feishu.cn/open-apis/message/v4/send/"

    headers = {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
    }
    req_body = {
        "chat_id": chat_id,
        "msg_type": "post",
        "content": content_
    }

    data = bytes(json.dumps(req_body), encoding='utf8')
    req = request.Request(url=url, data=data, headers=headers, method='POST')
    try:
        response = request.urlopen(req)
    except Exception as e:
        print(e.read().decode())
        return

    rsp_body = response.read().decode('utf-8')
    rsp_dict = json.loads(rsp_body)
    code = rsp_dict.get("code", -1)
    if code != 0:
        print("send message error, code = ", code, ", msg =", rsp_dict.get("msg", ""))


APP_ID = sys.argv[1]
APP_SECRET = sys.argv[2]
run_id = sys.argv[3]
token = get_tenant_access_token()
url = "https://github.com/RedrockMobile/CyxbsMobile_Android/actions/runs/"+run_id

content = {
    "post": {
        "zh_cn": {
            "title": "Pull Request自动打包完成",
            "content": [
                [
                    {
                        "tag": "text",
                        "text": "打包页面:"
                    }
                ],
                [
                    {
                        "tag": "a",
                        "text": url,
                        "href": url
                    }
                ]
            ]
        }
    }
}
send_message(token, "oc_6cf6bfaf7d93ec9d0090ac957ac6dda3", content)
