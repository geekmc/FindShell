# FindShell

![](https://img.shields.io/badge/build-passing-brightgreen)
![](https://img.shields.io/badge/Java-8-red)

## 介绍 

这是一个自动的内存马查杀工具，可以用于普通内存马和`Java Agent`内存马

尤其针对难以查杀的`Java Agent`型内存马，例如冰蝎等主流工具的内存马都是`Java Agent`

主要分为以下四步：

- 利用`JDK`提供的`sa-jdi`的`API`基于黑名单`dump`存在于`JVM`中**真正的**字节码
- 这种字节码在很多情况下是非法的，所以我修改了`ASM`源码以分析非法字节码
- 基于`ASM`做普通的分析和模拟栈帧做深入的分析
- 如果发现内存马将会尝试自动修复目标（利用`Java Agent`动态恢复原始字节码）

## 检测

基本检测：`java -jar FindShell.jar --pid [目标JVM的PID]`

这个`pid`的获取方式可以通过`jps`命令找到你需要的目标`JVM`的`pid`

这种情况下默认必检测的类有以下四个（最常见的`Java Agent`内存马出现的地方）

|                        类名                         |    方法名    | 
|:-------------------------------------------------:|:---------:|
|          javax/servlet/http/HttpServlet           |  service  |
|  org/apache/catalina/core/ApplicationFilterChain  | doFilter  |
| org/springframework/web/servlet/DispatcherServlet | doService |
|    org/apache/tomcat/websocket/server/WsFilter    | doFilter  |

并对以下的黑名单类进行检测（如果类名出现关键字则`dump`并分析该类字节码）

```java
keyword.add("shell");
keyword.add("memshell");
keyword.add("agentshell");
keyword.add("exploit");
keyword.add("payload");
keyword.add("rebeyond");
keyword.add("metasploit");
```

注意：

- 修改`org.sec.Constant`代码可以自定义黑名单和关键字
- 加入`--debug`参数保留从`JVM`中`dump`出的字节码供自行分析
- 并不是所有类的字节码都可以`dump`成功，但常见的这些类测试中没问题

## 修复

目前仅做了`Java Agent`内存马的自动修复，支持最常见的`HttpServlet`和`ApplicationFilterChain`

检测和修复：`java -jar FindShell.jar --pid [PID] --repair`

根目录存在一个`RepairAgent.jar`文件，这不属于该项目，但我将代码放在`org.sec.repair`包中供参考

**注意：修复手段仅靶机测试成功，在真实环境中使用请慎重**

## 原理

- 为什么不直接用`Java Agent`或`Alibaba Arthas`工具对`JVM`当前字节码进行`dump`

`Java Agent`内存马是调用`redefineClass`方法对字节码进行修改的。而调用`retransformClass`方法的时候参数中的字节码并不是调用`redefineClass`后被修改的类的字节码。对于冰蝎来讲，根本无法获取被冰蝎修改后类的字节码。我们自己写`Java Agent`清除内存马的时候，同样也是无法获取到被`redefineClass`修改后的字节码，只能获取到被`retransformClass`修改后的字节码。通过`Javaassist`等`ASM`工具获取到类的字节码，也只是读取磁盘上响应类的字节码，而不是`JVM`中的字节码

- 那么怎样获得存在于`JVM`中**真正的**字节码

之前有宽字节安全的师傅提到利用`sa-jdi`工具对真正的当前字节码进行`dump`后反编译结合人工分析，该工具也是基于`JDK`自带的`sa-jdi`库实现的，不过加入了一些过滤的选项

- 什么情况下这样的字节码为什么是非法的

当目标类存在`lambda`表达式的时候会导致非法字节码，具体可以参考我的文章

- 修改了哪些源码以解析非法字节码

参考连接：[修改源码说明](https://github.com/4ra1n/FindShell/blob/master/src/main/java/org/objectweb/asm/README.md)

- 为什么要结合普通字节码分析和模拟栈帧分析两种呢

因为`Runtime.exec`这种调用很不常见且过程简单，用普通的字节码分析即可解决。但是冰蝎的反射调用`defineClass`并反射`invoke`以实现代码执行效果的方式，过程比较复杂，且反射调用是程序中的常见功能，简单的分析会导致误报

- 什么是模拟栈帧分析

参考文章：[详解Java自动代码审计工具实现](https://tttang.com/archive/1334/) 和 [基于污点分析的JSP Webshell检测](https://xz.aliyun.com/t/10622)

## 参考与感谢

https://mp.weixin.qq.com/s/Whta6akjaZamc3nOY1Tvxg#at

占位，先知社区文章审核中...

## 免责声明

**工具仅用于安全研究以，由于使用该工具造成的任何后果使用者负责**