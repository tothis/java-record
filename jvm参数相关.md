### 查看jvm运行时参数指令
1. -XX:+PrintFlagslnitial -version -开启查看运行时初始化参数
2. -XX:+PrintFlagsFinal -version -开启查看运行时最终参数
***
### 参数类型

1. 标准参数 准参数在各个jvm版本中基本不变
* -help
* -server -client
* -version -showversion
* -cp -classpath

2. X参数 非标准化参数 在各个jvm版主中有可能会变 但是变化比较小
* -Xint 解释执行
* -Xcomp 第一次使用就编译成本地代码
* -Xmixed 混合模式 JVM自己来决定是否编译成本地代码

3. XX参数 非标准化参数 变化比较大 使用的时候需要根据jvm版本查询相关文档

3.1. Boolean类型

格式|说明|例子
-|-|-
-XX:[+-]<name>|+代表启用 -代表禁用 name代表参数名|-XX:+UseG1GC-启用G1垃圾回收器

3.2. 非Boolean类型

格式|说明|例子
-|-|-
-XX:<name>=<value>|name代表属性名 value代表值|-XX:MaxGCPauseMillis-毫秒:指定垃圾回收时的最长暂停时间

3.3. 简写类型

全写|简写
-|-
-XX:lnitialHeapSize=1024|-Xms=1024(初始化堆大小)
-XX:MaxHeapSize=1024|-Xmx=1024(最大堆大小)
***
### 导出内存映像文件方式
1. 内存溢出自动导出 当发生内存溢出的时候自动导出 需要设置两个jvm参数

参数|说明
--|--
-XX:+HeapDumpOnOutOfMemoryError|开启内存溢出自动导出功能
-XX:HeapDumpPath=./|设置导出目录 只有开启内存溢出自动导出才能生效

2. 使用jmap命令手动导出

命令|说明|
--|--|
jmap-dump:format=b,file=heap.hprof 16940|file=输出映像文件名 16940 当前运行程序的jar端口