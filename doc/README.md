# 说明文档

## 如何构建服务镜像

1. 下载源码 git clone https://github.com/dupengcheng/gzzd.git
2. 构建并安装 到本地仓库 mvn clean package install
3. 切换ui 服务目录  cd serverside/serverside-ui/
4. 构建 ui 服务镜像 mvn package jib:dockerBuild -am
5. 打 tag 并推送到你的镜像仓库 
```bash
git clone https://github.com/dupengcheng/gzzd.git
mvn clean package install
cd serverside/serverside-ui/
mvn package jib:dockerBuild -am
```

其中第4步，也可以用 jib:build 来直接推到你的镜像仓库，需要修改serverside/serverside-ui/pom.xml里to.image配置，并且在settings.xml配置镜像仓库地址和用户名密码。

## 如何本地调试  
### 启动 server 端
1. 找到 ServersideApplication 类
2. 配置参数
```yaml
masterUrl: "you k8s api server url"
token: "you k8s api server token"
namespace: "you k8s namespace "
```
3. 启动 ServersideApplication
4. 点右上角退出，登录admin/123456
5. 可以看到应用列表
6. 切换到主机信息，点击任意实例，提示agent未注册，其实后台已经调用exec接口在注册了，等1-3秒，jvm信息就显示出来了
7. 如果上一步一直不成功，进入对应容器查看日志，


## 如何在集群部署
我是用 kubectl create deploy/svc 这样的命令创建的。但是这样在 nodeport 确定后需要修改端口配置，重启一下服务，有点难说明白。
所以我导出了一份 yaml 文件， nodeport 已经在文件里定了，除非发生端口冲突。



