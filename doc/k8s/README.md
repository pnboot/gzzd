


修改 deployment里的env参数
如果service.yaml里的端口没有冲突，就不要改了，如果端口冲突了，记得映射关系


9091 是 ui 的web端口,
9013 是给浏览器访问 websocket 端口
9014 是给 agent 访问 websocket 端口


kubectl -n xx create -f yam/*

如果没有正常拉起 agent，看一下日志，我把拉起 agent 的命令打印了

如果修改了代码，记得先到全局目录执行 mvn clean package install  我这边 mvn -am -pl 好像没用，必须到根目录构建一下


