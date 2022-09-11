# 事务 README

## 简介

从课表里脱离出来的事务,独立成一个模块,逻辑层由[@985892345](https://github.com/985892345)编写,业务层由[@WhiteNight123](https://github.com/WhiteNight123)编写

## 架构层级

添加事务（affairt模块）采用了**`mvvm架构`**，项目结构按照mvvm的标准大致分为：

- **`Model层`** : 这里涉及到的数据类比较多(但其实他们都是一个类😂,需要做好转换工作)
  - `AddAffairBean`是网络请求返回的数据类,
  - `AffairEntity`本地数据库储存的数据类,
  - `LocalAddAffairEntity`本地临时数据库储存的数据类
  - `AffairEditArgs`从外界启动事务传递的数据类
  - `AffairAdapterData`Rv的数据类
- **`View层`** : 包含1个Activity、2个Fragment以及若干个Adapter等
- **`ViewModel层`** : 包含Activity以及Fragment各自的ViewModel
- **`Repository层()`** : 维护了一个本地临时数据,涉及RxJava的复杂操作,每次请求都以本地临时数据为主,防止数据错乱

### 运行流程

获取事务：

1. 先检查是否有本地临时数据，只有本地临时数据全部上传后才能下载新的数据

2. 去拿远端数据并插入数据库

3. 上游失败了就取本地数据，可能是网络失败，也可能是本地临时上传事务失败

   他,

添加事务:

1. 上传到远端,如果成功则插入数据库,如果失败,则插入本地临时数据库

更新事务:

1. 先检查是否有本地临时数据，只有本地临时数据全部上传后才能下载新的数据
2. 如果成功,更新远端数据和本地数据库
3. 如果失败,则插入本地临时数据库和数据库




## 项目难点

### 事务的Repository层

本地临时保存的数据库,所有操作之前都要先上传本地临时数据库

本地临时数据库包含3条干流(添加事务,更新事务,删除事务),和若干条小流

以添加事务为例:

1. 先删除自身的Add数据
2. 上传到远端,如果成功,则更新数据库的数据,如果失败,就把之前删除的数据给插回去
3. 合并成Flowable,再转换成Completable
4. 最后将3条干流汇成一条主流

### 花里胡哨的动画

由以前Activity里的`TransitionManager`改成了Fragment中的Transition,使用Transition的`Fade(),Slide(),ChangeBounds()`和一个自定义的`Transition`(实现了共享元素动画的效果),
流式布局采用Rv+flexbox,使用ListAdapter,享受丝滑般的动画

## 特殊设计

### 过多的数据类

里面有很多数据类的转换,[见model层](#架构层级),需要做好转换
