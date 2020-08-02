#!/bin/bash

commmitMsg=$(cat "$1")

result=$(echo "$commmitMsg"|grep "\[.\+].\+")
middle=${commmitMsg%%]*}
msgType=${middle#*[}
typeArray=("fix:bug修复" "feature:需求" "optimize:优化" "release:版本升级" "style:代码格式调整，不涉及代码更改")

if [ "$result" == "" ]; then
    printf "********************************
提交格式有误，请确认格式是否为:
[type]title

description:
其中description可选，提交简单可只写[type]title
********************************"
    exit 1
    else
      isExist=0
      for i in "${typeArray[@]}" ; do
          if [ "${i%%:*}" == "${msgType}" ]; then
              isExist=1
              break
          fi
      done
      if ! [ $isExist == 1 ]; then
          echo "未找到对应type，请确认是否在以下type中"
          for i in "${typeArray[@]}" ; do
              echo "$i"
          done
          exit 1
      fi
fi