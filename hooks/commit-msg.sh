#!/bin/bash

###############################################真正返回值设定####################################################
#真正返回值记录的变量
#若要编辑该脚本，请不要直接再脚本内直接使用exit退出
#出错请赋值该变量为1即可
realLastReturn=0
#最好使用该方法对realLastReturn赋值，不然可能会覆盖别人的非零返回值
function setRealLastReturn() {
    if [[ $1 = 1 ]]; then
        realLastReturn=1
    fi
#    如果git hook检查不再适用，且暂时没时间修改该脚本，请取消下面一行注释
#    realLastReturn=0
}

###############################################Commit消息格式检查####################################################
commitMsg=$(cat "$1")

result=$(echo "$commitMsg"|grep "\[.\+].\+")
middle=${commitMsg%%]*}
msgType=${middle#*[}
typeArray=("fix:bug修复" "feature:需求" "optimize:优化" "release:版本升级" "style:代码格式调整，不涉及代码更改")
typeShow=$(for i in "${typeArray[@]}" ; do printf "$i ";done)
merge=$(echo "$commitMsg"|grep "^Merge")
if ! [[ "$merge" == "" ]]; then
    echo "检测到为自动merge提交信息，忽略提交格式检查"
elif [[ "$result" == "" ]]; then
    echo "提交格式有误，标准格式为:
[type]title

description:
********************************************************************
description为可选，简单提交可只写[type]title"
    echo "*************************type类型提示*******************************"
    echo "${typeShow}"
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
          echo "未找到对应type，请确认是否在以下type中"
          echo ${typeShow}
          setRealLastReturn 1
      fi
fi


###############################################Drawable资源命名检查####################################################

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
    #获取最外层那个节点名
    startTag=$(cat $1|sed -n "/<[^?].*/p"|sed -n 1p|sed "s/.*<\([^ ]*\).*/\1/g")
#    endTag=$(cat $1|sed -n "/<\/.*>/p"|sed -n '$p'|sed "s/.*<\/[ ]*\([^ ]*\)[ ]*>.*/\1/g")
    #防止某些
    echo "$startTag"
}

drawableFirstTips="*******************以下资源文件有问题需要解决***********************"
function tipsEcho() {
    if ! [[ ${drawableFirstTips} == "" ]]; then
        echo ${drawableFirstTips}
        drawableFirstTips=""
    fi
    tipsName=$(basename $1)
    tipsModuleName=$(echo $(cd $(dirname $1);pwd)|sed "s/.*\/\(.*\)\/src\/.*/\1/g")
    tipsModuleName=${tipsModuleName//"module_"/}
    tipsModuleName=${tipsModuleName//"lib_"/}
    tipsTagName=$2
    echo "[ $(cd $(dirname $1);pwd)/$tipsName 新增资源文件命名格式错误 ]"
    echo "[ ${tipsName} 文件前缀应修改为：${tipsModuleName}_${tipsTagName}_ ]"
}

#处理xml的Drawable,获取一个参数，文件路径，相对和绝对都行
function handDrawable() {
    returnValue=0
    name=$(basename $1)
    suffix=${name##*.}
    moduleNamePrefix=$(echo $(cd $(dirname $1);pwd)|sed "s/.*\/\(.*\)\/src\/.*/\1/g")
    moduleNamePrefix=${moduleNamePrefix//"module_"/}
    moduleNamePrefix=${moduleNamePrefix//"lib_"/}
    if ! [[ $(echo ${name}|grep "ic_launcher") = "" ]]; then
        # 如果检测到是app图标，放行
        return ${returnValue}
    elif [[ ${suffix} = "xml" ]]; then
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

#判断drawable主要流程
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

###############################################退出脚本,请确保脚本没有中途exit####################################################
if [[ $realLastReturn == 1 ]]; then
    echo ""
    echo "提交失败，请修复上述问题后再提交"
fi
exit ${realLastReturn}
