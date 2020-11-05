
## 启动方法
1. 修改 deployment里的env参数
    1. 如果service.yaml里的端口没有冲突，就不要改了，如果端口冲突了，记得映射关系就行。
    2. 9091 是 ui 的web端口
    3. 9013 是给浏览器访问 websocket 端口
    4. 9014 是给 agent 访问 websocket 端口
2. kubectl -n xx create -f yam/*
3. 如果没有正常拉起 agent，看一下日志，我把拉起 agent 的命令打印了

```
- env:
            # k8s的 apiserver 地址，兼容了http不带s的情况
            - name: masterUrl 
              value: "https://111.111.111.111:6443"
            # k8s的 apiserver token， 可以留空字符串
            - name: token     
              value: ""
            # 如果重新打包了，接入配置中心等，用于指定运行的profile
            - name: spring.profiles.active
              value: test
            # k8s 空间
            - name: namespace
              value: test
            # ingress controller 的外部IP，因为是用 nodeport 暴露
            - name: proxy.server.uiIp
              value: "111.111.111.112"
            # 和 service 对应的 nodeport
            - name: proxy.server.nodePort
              value: "31113"
            # 集群内的访问地址，如果没有改 serviceName 则不需要修改
            - name: proxy.server.proxyIp
              value: "serverside-ui"
```



