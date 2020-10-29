# gzzd 

## 故障诊断介绍

java应用故障诊断工具预研 demo ，基于开源项目bistoury修改，99.999%代码保持一致，原项目明显是为多节点大集群设计，对于小集群来说配置和调试略复杂。
改动的目的是简化少量节点和服务时开发和部署，支持kubernetes集群部署。

故障诊断工具的使用场景是定位解决一些极端情况下的问题，作为传统策略无法分析故障时的诊断工具，我们并不追求日常场景下的高频使用，还是建议大家好好写代码，认真做测试。

通过在线诊断工具发现、定位和解决应用问题。主要手段包括：实例信息的全方位展示、故障点的精确观察、故障代码实时修改和热加载。

这份源码作为 kubernetes 支持预研测试的 demo，基本功能测试可用，如果发现问题我会及时更新。

如果使用中遇到问题，可以联系我，我会提供力所能及的交流和帮助。

## 由来
bistoury 的功能和架构设计都十分优秀，但也存在源码复杂、组件依赖多、配置多的情况。入门有难度，有一点部署和维护门槛，尤其对于小集群少量服务的小团队。

我做了一些改动，试图保留优秀的功能设计，同时降低入门门槛，（真是想的美啊）。如果你看到这里，也试图简化或者丰富，不要藏着了，一起来吧。

改动的目的是简化开发和部署，bistoury 原项目地址为： https://github.com/qunarcorp/bistoury.git

主要做了以下改动：
1. 修改启动类，改为 springboot 启动，删除 conf 文件，默认写在 map 里或者配置在 yaml 里
2. 整理包目录层次结构
3. 合并 ui 和 proxy 
4. 删除 zookeeper 和 gitlab 依赖
5. 增加 kubernetes 支持，点击实例列表时，自动在对应 pod 执行命令启动 agent，等待 1-2 秒即可看到 jvm 信息
6. 修改 ClassPathLookUp 配置 useDefaultClassPath 始终为 true
7. 修改 bistoury-agent-env.sh ，配置 BISTOURY_PROXY_HOST 为 service 地址，增加 -Dbistoury.store.db=sqlite ，避免 alpine 缺包无法使用 rocksdb
8. 修改 LoginController，默认使用 admin/123456 登录，所以即使不配置 mysql 地址也没有关系

## 启动

启动只需 ServersideApplication ，配置好 masterUrl 和 token ，调整 start_agent.sh 脚本的 proxy 地址为本地端口，且保证pod内能够访问到本地，会自己调 exec 拉起 agent (大概需要1-3秒)。

### 配置说明
整体流程为
browser <-> serverside-ui <-> agent <-> target_jvm

1. 浏览器访问 serverside-ui 的 http 和 websocket 端口
在本地调试时是两个独立端口，在 k8s 集群里如果使用 nodeport 提供服务，也是两个独立端口，也可以使用 Ingress 提供域名。
2. serverside-ui 到 agent 的端口 


### 本地启动
1. 在根目录执行 mvn clean package install ,打包所有模块到本地 maven 仓库
2. 配置 kubernetes api server 参数，和 websocket nodeport 参数
3. 运行 ServersideApplication 启动类
4. 访问 127.0.0.1:9091 端口
5. 登录 admin/123456
6. 切换到主机信息，选择一个实例
7. 稍等3-5秒，即可看到实例信息 

异常情况定位
1. 如果一直没有反应，可以在 ProxyConfigForAgentGetController 类的 getProxyWebSocketUrl 和 getProxyConfig 方法打断点，观察拉起 agent 是否顺利
2. 可以到目标 pod 的 /kfz/gzzd/agent-bin/logs 看日志


### 集群部署方法：

以 dev 空间的 ssui-dev-platform 为例 ,首先需要构建 ui 的镜像包，也可以用我打好的镜像


  
