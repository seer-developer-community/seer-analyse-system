#seer-operation

## 准备环境

java8环境、本地需要运行wallet-cli、本地MYSQL数据库

## 1.下载release的v1.0版本的压缩包 operation-distribution.zip

解压缩后运行

./bin/start.sh >> run.log 2>&1 &

## 2.查看日志

每天会生成一个日志文件

cd ~/logs
tail -fn 1000 seer.xxxx-xx-xx.log
