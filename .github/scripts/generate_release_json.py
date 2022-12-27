import sys, requests, re, os, json, time

base_url = sys.argv[1]
token = sys.argv[2]
pr_content = sys.argv[3]

try:
    # 更新信息
    update_content = pr_content.split("update_content=")[0]
    # 更新时间
    update_time = pr_content.split("update_time=")[1].split("update_content=")[0]
    # 示例: update_time=2022 12 14 0
    update_time = time.mktime(time.strptime(update_time, "%Y %m %d %H"))
except:
    raise RuntimeError("pr信息格式错误, 终止发版操作 (example: update_time=2022 12 14 0update_ontent=xxxxxx)")

def upload_apk(path: str) -> str:
    resp = requests.post(base_url + "/upload_apk", files={ "file": open(path, "rb") }, headers={ "token": token }).json()
    if not resp["ok"] :
        raise RuntimeError("apk文件上传失败")
    return resp["data"]

def upload_data(path: str) -> str:
    resp = requests.post(base_url + "/upload_data", files={ "file": open(path, "rb") }, headers={ "token": token }).json()
    if not resp["ok"] :
        raise RuntimeError("apk文件上传失败")
    return resp["data"]

# 拿到版本名和版本号
with open("./module_app/build/generated/source/buildConfig/release/com/mredrock/cyxbs/BuildConfig.java") as f:
    code_content = f.read()

version_name = re.search("public static final String VERSION_NAME = \"(?P<name>\S+)\"", code_content, re.M).group("name")
version_code = re.search("public static final int VERSION_CODE = (?P<code>\d+)", code_content, re.M).group("code")

# 版本号校验
if not re.match("\d+\.\d+\.\d+$", version_name, re.M) :
    raise RuntimeError(f"版本名称 {version_name} 不符合命名规范！")
packages = {}
for file_name in os.listdir("./module_app/build/channel"):
    channel = file_name.split("-")[2]
    packages[channel] = upload_apk(f"./module_app/build/channel/{file_name}")

# 混淆映射文件
packages["mappings"] = upload_data("./build-logic/core/proguardMapping.txt")

content_json = {
    "apk_url": packages, 
    "update_content": update_content, 
    "version_code": int(version_code), 
    "version_name": version_name 
}

release_json = { 
    "update_time": update_time,
    "content_json": content_json
}

# 将发包信息写出到文件中，之后再由 CI 上传为 artifact
with open("./release.json") as f:
    f.write(json.dumps(release_json, indent=2, sort_keys=True, ensure_ascii=False))

print(release_json)