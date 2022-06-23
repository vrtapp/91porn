# pornbot91_py
91 video download, send telegram


 [中文](./README.md) 

###  特点

Lustful video link support (supplements author deleted videos)

**Try to handle it in an asynchronous way, the download speed is greatly improved**, and the actual measurement is very fast (100M video, download + merge takes 18 seconds)

Compatible with old mp4, now m3u8

Crack the playback restrictions of 91 videos, theoretically unlimited downloads

Add a Chinese word segmentation tag to the title to solve the problem of telegram search in Chinese

Retry mechanism, network timeout retry

Send a link to the bot to `get the real video address` and `download the video`

Docker pre-installed environment, easy to replace services, one-click start


### docker run



#### Install docker
```
curl -fsSL get.docker.com -o get-docker.sh && sh get-docker.sh --mirror Aliyun&&systemctl enable docker&&systemctl start docker

```

#### Install docker-compose

```yaml
curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose &&chmod +x /usr/local/bin/docker-compose
```


### Deploy
Create a directory /pybot
```yaml
mkdir /pybot
```
### Edit docker-compose.yml

```angular2html
      #Windows needs to restart the computer to configure environment variables
      REDIS_HOST: 11.11.22.333
      REDIS_PORT: 16379
      REDIS_PASS: 424243
      API_ID: 21231221
      API_HASH: *************************
      BOT_TOKEN: *****:**************************
      #Group id of the group to which the timed task is sent(@get_id_bot,It can be accessed here
It is available here
Available here)
      GROUP_ID: 121231311
```

### Startup project

```yaml
docker-compose up 
```

### run locally
python3.10以上
```
pip install -r requirements.txt
```

Modify proxy

```
python pornbot.py
```


### Test

1.send /start to the bot

get a reply  `********`

send link test
 ![image](https://user-images.githubusercontent.com/48782751/159890884-d65a2528-e7fc-4be3-a981-fa7608072467.png)

