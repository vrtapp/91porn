# 基于telegramBot、Ffmpeg、MP4Box、UnitPage的爬取视频、发送视频的机器人

91视频爬取

# 其他语言实现不进行公开了，仅供学习爬虫思路参考。

###  特点

好色视频链接支持（补充作者删除的视频）

**尽量异步的方式处理,下载速度大幅提升**,实测很快(100M视频,下载+合并用时18秒)

兼容旧版mp4，现在是m3u8

破解91视频的播放限制、理论上可以无限下载

为标题添加中文分词标签，解决电报对中文搜索的问题

重试机制，网络超时重试

向机器人发送链接，可以 `获取视频真实地址` 并 `下载视频`

docker预装环境，方便更换服务，一键启动


向机器人([@porn_91Bot](https://t.me/porn_91Bot))发送链接，可以 `获取视频真实地址` 并 `下载视频`

## 环境

java11

电报机器人申请

MP4Box、Ffmpeg安装。

MP4Box 安装
https://gpac.wp.imt.fr/downloads/

https://github.com/gpac/gpac/wiki/GPAC-Build-Guide-for-Linux

debian10编译好的: https://github.com/jw-star/myFigurebed/releases/download/1.00/gpac.tar.gz


## 修改配置(需要修改的地方)

### 测试环境

application.yml

```yaml
spring:
profiles:
      active: prod
```





 application-dev.yml

```yaml

myappconfig:
  #是否开启代理
  proxy_on: true
  #ffmpeg 路径
  ffmpeg_root: E:\\Utils\\ffmpeg\\bin\\
  #MP4Box 路径
  mp4box_root: E:\\Utils\\GPAC\\mp4box
  #临时文件路径
  fileroot: "F:\\m3u8JavaTest\\"
  #发送telegram目的id
  chat_id: "44444"
  bot_token: "44444444444444444444"
logging:
  file:
    path: E:\LianXi\demo\target\
    name: porn.log
  level:
    root: info
```
### 正式环境

application.yml
```yaml
spring:
profiles:
    active: prod
```

application-prod.yml

```yaml
myappconfig:
  proxy_on: false
  ffmpeg_root: "/usr/bin/"
  mp4box_root: " /root/gpac_public/bin/gcc/MP4Box"
  fileroot: "/root/video/"
  chat_id: "44444444"
  bot_token: "44444444444"
logging:
  file:
    path: /root/video/
    name: porn.log
  level:
    root: info
```
