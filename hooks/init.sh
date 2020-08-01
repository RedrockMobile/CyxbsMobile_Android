#!/bin/sh

function isCmdExist() {
	local cmd="$1"
	if [[ -z "$cmd" ]]; then
		echo "Usage isCmdExist yourCmd"
		return 1
	fi
	which "$cmd" >/dev/null 2>&1
	if [[ $? -eq 0 ]]; then
		return 0
	fi
	return 2
}

#判断kt脚本环境是否已经安装
if ! isCmdExist kscript;
  then
    echo "*******kt脚本环境未安装********"
    curl -s "https://get.sdkman.io" | bash
    source ~/.bash_profile
    sdk install kotlin
    sdk install kscript
    kscript --help
    echo "*******kt脚本环境安装成功********"
fi

#脚本所在目录
shellPath=$(cd "$(dirname "$0")";pwd)
#脚本所在上一级目录，这里一定要是主仓根目录
rootPath="$shellPath/.."

#寻找git hook文件放置目录
cd ${rootPath}
hooksArray=()
index=0
for hooks in `find . -name hooks -type d` ; do
   cd ${hooks}
   f=$(echo ${hooks} | grep ".git/")
   if ! [[  ${f} == "" ]]; then
      hooksArray[$index]=$(pwd)
      echo $(pwd)
      index=`expr ${index} + 1`
   fi
   cd ${rootPath}
done


#寻找脚本所在目录kts文件,并复制到当前仓库以及所有子仓库中
cd ${shellPath}
kts='kts'
for file in `ls` ; do
    suffix=${file##*.}
#    找到后缀为kts的脚本
    if test ${suffix} == "kts"; then
        chmod u+x ${file}
        for hookPath in ${hooksArray[@]} ; do
            newPath="$hookPath/${file%.*}"
            oldPath="$shellPath/${file}"
#            查看是否存在某hook文件，这里暂时删除了，便于每次更改
#            if [[ ! -f ${newPath} ]]; then
#                moduleName=$(basename $(dirname $(dirname ${hookPath})))
#                echo "${moduleName}仓库添加${file%.*}"
#                cp ${oldPath} ${newPath}
#            fi
            cp ${oldPath} ${newPath}
        done
    fi
done
cd ${rootPath}









