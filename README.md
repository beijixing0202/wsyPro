#1.Jacoco原理
Jacoco使用插桩的方式来记录覆盖率数据，是通过一个probe探针来注入
插桩有模式：
1. on-the-fly模式
jvm通过 -javaagent参数指定jar文件启动代理程序，代理程序在ClassLoader装载一个class前判断
是否修改class文件，并将探针插入class文件，探针不改变原有方法的行为，只是记录是否已经执行。
2. offine模式
在测试之前先对文件进行插桩，生成插过桩的class或jar包，测试插过桩的class和jar包，
生成覆盖率信息到文件，最后统一处理，生成报告。
3. on-the-fly和offline对比
on-the-fly更方便，无需提前插桩，无需考虑classpath设置问题
存在以下情况不适合使用on-the-fly模式：
- 不支持javaagent
- 无法设置JVM参数
- 字节码需要装换成其他虚拟机
   * Android虚拟机不同与服务器上的JVM，它所支持的字节码必须经过处理支持Android Dalvik等专用虚拟机，所以插桩必须在处理之前完成，即离线插桩模式。
   * Android虚拟机没有配置JVM 配置项的机制，所以应用启动时没有机会直接配置dump输出方式。
   * Android代码覆盖率统计配置：https://blog.csdn.net/weixin_36032446/article/details/119169568
- 动态修改字节码过程和其他agent冲突
- 无法自定义用户加载类
# 2.jacoco应用：
## 1.下载jacoco：https://www.jacoco.org/jacoco/index.html
 相关jar包
  - jacocoagent：运行时启动tcp服务监控代码覆盖，dump出覆盖率数据。启动应用时主要用来插桩的jar包
  - jacocoant：jacoco的任务是ant驱动的，所以这个包用来执行jacoco的任务，向tcp服务发送请求。
  - jacococli：导出覆盖率记录的exec文件，生成覆盖率报告，可以生成html格式或者xml格式的覆盖率报告。

## 2.启动方式
1. 文件方式：output=file
    需要mvn打jar包，或者tomcat打war包
    在每次服务结束时统一收集覆盖率dump
    java -javaagent:E:\jacocoTest\jacocoagent.jar=includes=*,output=file,append=true,destfile=E:\jacocoTest\jacoco.exec -jar E:\jacocoTest\wsy-0.0.1.jar
2. tcp服务： output=tcpserver
    开放一个tcp端口,可以在过程中收集覆盖率dump
    java -javaagent:/tmp/jacoco/lib/jacocoagent.jar=includes=*,output=tcpserver,port=6300,address=localhost,append=true -jar wsy-0.0.1.jar
    需要通过jacococli.jar 来获取dump
    java -jar jacococli.jar dump --address localhost --port 6300 --destfile ./jacoco-demo.exec
3. 客户端方式：
   https://blog.csdn.net/Javauthor/article/details/115483534
4. cli包将.exec文件生成覆盖率报告：
   java -jar jacococli.jar report jacoco-demo.exec --classfiles  E:\自动化脚本\wsyPro\target\classes  --sourcefiles E:\自动化脚本\wsyPro\src\main\java  --html  html-report --xml report.xml --encoding=utf-8

# 3.Jacoco增量覆盖
## 1.增量覆盖：两次提交之间有哪些代码或分支没有被覆盖
目的：检测同一个测试用例在修改前后代码上得行覆盖情况
假设两次提交代码变更如下：
```
if （x>100）
    - print("wsy")
    + print("wang")
else
    - print("123")
    + print("456")
```
每行代码有三种状态：+ 、-、不变
修改前后执行同一个测试用例，每行有四种状态：修改前覆盖\修改前没有覆盖，修改后覆盖\修改后没有覆盖。
比较重要的有：新增代码没有覆盖，新增代码覆盖了，不变的代码修改前覆盖，修改后没有覆盖
### 计算增量代码具体步骤：
- 计算出两个版本的差异代码（使用git或svn提供的接口）
- 将差异代码在report阶段传给jacoco
- 修改jacoco源码，生成报告时判断diamante是否是增量代码，只有增量代码采取生成报告
## 2. 增量应用：
  * jacoco 二开：https://gitee.com/Dray/jacoco.git
  * 增量代码获取：https://gitee.com/Dray/code-diff.git
###使用方法
  1. jacoco客户端，收集信息
```shell
   java -javaagent:jacocoagent.jar=includes=*,output=tcpserver,port=6300,address=localhost,append=true -jar wsy-0.0.1.jar
```
  2. 使用二开cli包生成exec文件
```shell
  java -jar cli-0.8.7-diff.jar dump --address localhost --port 6300 --destfile jacoco-demo.exec
```
 3. 获取两次提交代码差异
```shell
  java -jar code-diff.jar  启动diff项目
  网址：http://127.0.0.1:8085/doc.html
```
 4. 二开cli包生成report增量报表
 ```shell
    java -jar cli-0.8.7-diff.jar report jacoco-demo.exec --classfiles  E:\自动化脚本\wsyPro\target\classes  --sourcefiles E:\自动化脚本\wsyPro\src\main\java  --html  html-report --xml report.xml --diffcode "" --encoding=utf-8
    --diffcode ""  全量覆盖报告则不上传该参数
```
 


多服务exec文件采集、合并、下载实现
https://blog.csdn.net/Javauthor/article/details/115483534
Jacoco测试报告统计维度定义：
行覆盖率：度量被测程序的每行代码是否被执行，判断标准行中是否至少有一个指令被执行。
类覆盖率：度量计算class类文件是否被执行。
分支覆盖率：度量if和switch语句的分支覆盖情况，计算一个方法里面的总分支数，确定执行和不执行的 分支数量。
方法覆盖率：度量被测程序的方法执行情况，是否执行取决于方法中是否有至少一个指令被执行。
指令覆盖：计数单元是单个java二进制代码指令，指令覆盖率提供了代码是否被执行的信息，度量完全 独立源码格式。
圈复杂度：在（线性）组合中，计算在一个方法里面所有可能路径的最小数目，缺失的复杂度同样表示测 试案例没有完全覆盖到这个模块。