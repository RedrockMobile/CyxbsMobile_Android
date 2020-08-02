#!/bin/bash

#脚本所在目录
shellPath=$(cd "$(dirname "$0")";pwd)

#寻找脚本所在目录文件,并复制到当前仓库以及所有子仓库中
cd "${shellPath}"
for file in `ls` ; do
    chmod u+x ${file}
done
cd "${rootPath}"









