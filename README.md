# dubbo-trace
基于dubbo3.x开发的简单链路追踪组件

# 使用说明

## 1.clone项目
git clone https://github.com/zbrave429/dubbo-trace.git
## 2.打包
maven install
## 3.maven工程引入依赖
```java
<dependency>
    <groupId>com.brave</groupId>
    <artifactId>dubbo-trace</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
## 4.日志输出配置
日志输出格式内增加配置 %X{traceId} %X{spanId}
- %X{traceId}：traceId参数
- %X{spanId}：spanId参数，调用树
## 5.服务入口调用初始化方法
- http接口可以通过拦截器内调用
```java
/**
 * IdGenEnum idGenEnum id生成器类型，目前支持两种
 *  UUID - 通过UUID生成的lang整形19位带符号数字
 *  CURRENT_TIME - 通过（时间戳11位 + 自增ID4位 + 随机数4位）生成的19位字符串
 * 
 * prefix 前缀，拼接在系统内置的算法生成的字符串之前，CURRENT_TIME模式下才有效，
 *          用来增强traceId的唯一性，例如：prefix = IP + APPKEY
 */

TraceContext.init(IdGenEnum idGenEnum, String prefix);
```
## 6.线程池使用
为了保证trace信息在子线程内进行传递，统一使用thread包内的AsyncTaskFactory提交延时任务


