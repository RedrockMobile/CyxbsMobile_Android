import os, json, requests, sys, time

base_url = sys.argv[1]
token = sys.argv[2]

# 如果不存在release.json，说明已经超过90天没发新版了，不管
if os.path.exists("./release_json/release.json"):
    # 跟网络上的最新数据比对
    with open("./release_json/release.json") as f:
        info = json.loads(f.read())
        print("本地数据: ")
        print(info)
    content_json = info["content_json"]
    latest = requests.get(f"{base_url}/cyxbsAppUpdate.json").json()
    print("云端数据: ")
    print(latest)
    # 不相同说明有更新
    if latest["version_code"] != content_json["version_code"]:
        # 检查是否到时间了，到了就直接更新json
        if time.time() > info["update_time"]: 
            print("触发发版操作")
            print(requests.post(f"{base_url}/cyxbsAppUpdate.json", json=content_json, headers={ "token": token }).json())
        else:
            print("没到发版时间")
    else:
        print("已是最新版本")