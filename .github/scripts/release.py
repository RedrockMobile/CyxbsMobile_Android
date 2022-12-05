import sys, requests, re, os

base_url = sys.argv[1]
token = sys.argv[2]
update_content = sys.argv[3]

if update_content.startswith("update_content="):
    update_content = update_content.removeprefix("update_content=")
else:
    raise RuntimeError("pr信息格式错误, 终止发版操作 (请以 update_content= 开头)")

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

resp = requests.post(f"{base_url}/cyxbsAppUpdate.json", 
    json={ "apk_url": packages, "update_content": update_content, "version_code": int(version_code), "version_name": version_name }, 
    headers={ "token": token }).json()

print(resp)