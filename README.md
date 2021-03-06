# cloud

## 项目结构
```
gateway         网关
configurations    配置中心
services    基础服务
interfaces       基础服务rpc接口
common          公共包
service-api     第三方插件API接口
```

## Spring Cloud 基础包

```
服务器注册与发现: Nacos
网关: Spring Gateway
服务调度: Dubbo
流量监控: Alibaba Sentinel
分布式事务: Seata
队列: RabbitMq
负载: Ribbon
认证: Spring Security
```

## gateway-dynamic-route 路由配置

```
- id: usercenter
  uri: lb://usercenter
  predicates:
  - Path=/usercenter/**
  filters:
  - StripPrefix=1
- id: test
  uri: lb://test
  predicates:
  - Path=/test-server/**
  filters:
  - StripPrefix=1
```