#seer-operation

## 准备环境

java8环境、本地需要运行wallet-cli、本地MYSQL数据库

ubuntu安装java环境：

1. 下载release中的jdk1.8压缩包，上传至ubuntu的 home/xxx(xxx为当前用户目录)
2. mkdir ~/Java
3. tar -zxvf xxxx.tar.gz Java
4. 配置环境变量
```
vim /etc/profile

export JAVA_HOME=/home/xxx/Java/jdk1.8.0_161/
export JRE_HOME=$JAVA_HOME/jre
export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH
export CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib:.

source /etc/profile
```
5. java -version 检查版本和是否配置成功

## 1.下载release的v1.0版本的压缩包 operation-distribution.zip

解压缩后，进入解压后的目录运行

```
./bin/startup.sh >> run.log 2>&1 &
```

成功的话会提示：

```
(base) ubuntu@ip-172-131-43-119:~/operation$ ./bin/startup.sh >> run.log 2>&1 &
[1] 3174
(base) ubuntu@ip-172-131-43-119:~/operation$ 
[1]+  Done                    ./bin/startup.sh >> run.log 2>&1
```
## 2.查看日志

每天会生成一个日志文件

```
cd ~/logs
tail -fn 1000 seer.xxxx-xx-xx.log
```
