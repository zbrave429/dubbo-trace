# dubbo-trace
基于dubbo3.x开发的简单链路追踪组件

# 使用说明

## 1.git clone 

## 2.maven install

## 3.log config
日志输出格式内增加配置 %X{traceId} %X{spanId}
- %X{traceId}：traceId参数
- %X{spanId}：spanId参数，调用树
