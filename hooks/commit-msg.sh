#!/bin/bash

#真正返回值记录的变量
realLastReturn=0
function setRealLastReturn() {
    if [[ $1 = 1 ]]; then
        realLastReturn=1
    fi
#    如果git hook检查不再适用，且暂时没时间修改该脚本，请取消下面一行注释
#    realLastReturn=0
}

## blue to echo
function echoBlue(){
    echo -e "\033[34m$1\033[0m"
}

## green to echo
function echoGreen(){
    echo -e "\033[32m$1\033[0m"
}

## Error
function echoRed(){
    echo -e "\033[31m\033[01m$1\033[0m"
}
## warning
function echoYellow(){
    echo -e "\033[33m\033[01m$1\033[0m"
}


commitMsg=$(cat "$1")

result=$(echo "$commitMsg"|grep "\[.\+].\+")
middle=${commitMsg%%]*}
msgType=${middle#*[}
typeArray=("fix:bug修复" "feature:需求" "optimize:优化" "release:版本升级" "style:代码格式调整，不涉及代码更改")
typeShow=$(for i in "${typeArray[@]}" ; do printf "$i ";done)

if [[ "$result" == "" ]]; then
    echoRed "提交格式有误，标准格式为:
[type]title

description:
***********************************************************
description为可选，简单提交可只写[type]title"
    echoGreen "********************type类型提示****************************"
    echoGreen "${typeShow}"
    setRealLastReturn 1
    else
      isExist=0
      for i in "${typeArray[@]}" ; do
          if [[ "${i%%:*}" == "${msgType}" ]]; then
              isExist=1
              break
          fi
      done
      if ! [[ ${isExist} == 1 ]]; then
          echoRed "未找到对应type，请确认是否在以下type中"
          echoRed ${typeShow}
          setRealLastReturn 1
      fi
fi

#判断该文件是否为Drawable资源,接受一个参数，参数为文件路径，相对路径绝对路径都可
function isDrawable() {
    path=$(cd $(dirname $1);pwd)
    tag=$(echo "$path"|sed 's/.*\/\(.*\)/\1/g')
    if [[ ${tag%%-*} == "drawable" ]]; then
        return 1
    fi
    return 0
}

#获取xml最外层标签,获取一个参数，文件路径，相对和绝对都行
#由于不能返回字符串，所以请直接使用$(getXmlFirstTag 参数)
#切勿直接调用此函数
function getXmlFirstTag() {
    content=$(cat $1|sed  "s/\n//g" | sed ":label;N;s/\n//;b label")
    echo "$content"|sed "s/.*<[^?]\(.*\)>/\1/g"
}

function tipsEcho() {
    tipsName=$(basename $1)
    tipsModuleName=$(echo $(cd $(dirname $1);pwd)|sed "s/.*\/\(.*\)\/src\/.*/\1/g")
    tipsModuleName=${tipsModuleName//"module_"/}
    tipsModuleName=${tipsModuleName//"lib_"/}
    tipsTagName=$2
    echoRed "[ $(cd $(dirname $1);pwd)/$tipsName 新增资源文件命名格式错误 ]"
    echo "[ $(echoYellow ${tipsName}) 文件前缀应修改为：$(echoGreen "${tipsModuleName}_${tipsTagName}_") ]"
}

#处理xml的Drawable,获取一个参数，文件路径，相对和绝对都行
function handDrawable() {
    returnValue=0
    name=$(basename $1)
    suffix=${name##*.}
    moduleNamePrefix=$(echo $(cd $(dirname $1);pwd)|sed "s/.*\/\(.*\)\/src\/.*/\1/g")
    moduleNamePrefix=${moduleNamePrefix//"module_"/}
    moduleNamePrefix=${moduleNamePrefix//"lib_"/}
    if [[ ${suffix} = "xml" ]]; then
        firstTag=$(getXmlFirstTag $1)
        if [[ ${firstTag} == "vector" ]]; then
            isFit=$(echo ${name}|sed "s/${moduleNamePrefix}_ic_.*//g")
            if ! [[ "$isFit" = "" ]]; then
                tipsEcho $1 "ic"
                returnValue=1
            fi
        else
            realTag=${firstTag//-/_}
            isFit=$(echo ${name}|sed "s/${moduleNamePrefix}_${realTag}_.*//g")
            if ! [[ "$isFit" = "" ]]; then
                tipsEcho $1 ${realTag}
                returnValue=1
            fi
        fi
    elif [[ ${suffix} = "jpg" ]] \
        || [[ ${suffix} = "png" ]]\
        || [[ ${suffix} = "webp" ]]\
            ; then
        isFit=$(echo ${name}|sed "s/${moduleNamePrefix}_ic_.*//g")
            if ! [[ "$isFit" = "" ]]; then
                tipsEcho $1 "ic"
                returnValue=1
            fi
    fi
    return ${returnValue}
}

changeFile=`git status|grep "new file:"`
array=(${changeFile//new file:/})
for file in "${array[@]}" ; do
    #判断是不是drawable
    isDrawable ${file}
    if test $? -eq 1 ; then
        fileName=$(basename ${file})
        handDrawable ${file}
        setRealLastReturn $?
    fi
done

exit ${realLastReturn}
