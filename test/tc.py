# 首页列表爬取
import asyncio
import re

import aiohttp

from bs4 import BeautifulSoup
from faker import Faker
from pyppeteer import launch

import util

fake = Faker('zh_CN')


# 去除文件名中 非法的字符
def clean_file_name(filename: str):
    invalid_chars = '[\\\/:*?"<>|]'
    replace_char = '-'
    return re.sub(invalid_chars, replace_char, filename)


async def getVideoInfo91(url):
    print('得到的url:',url)
    try:
        browser, page = await ini_browser()
        await asyncio.wait_for(page.goto(url), timeout=30.0)
        await page._client.send("Page.stopLoading")

        await page.waitForSelector('.video-border')
        # 执行JS代码
        # evaluate页面跳转后 已经注入的JS代码会失效
        # evaluateOnNewDocument每次打开新页面注入
        strencode = await page.evaluate('''() => {
               return $(".video-border").html().match(/document.write([\s\S]*?);/)[1];
            }''')

        realM3u8 = await page.evaluate(" () => {return " + strencode + ".match(/src='([\s\S]*?)'/)[1];}")

        # imgUrl = await page.evaluate('''() => {
        #        return $(".video-border").html().match(/poster="([\s\S]*?)"/)[1]
        #     }''')

        # 判断是否高清
        length = await page.evaluate('''() => {
               return $("#videodetails-content > a:nth-child(2)").length
            }''')
        if int(length) > 0:
            if '.mp4' in realM3u8:
                realM3u8 = realM3u8.replace('/mp43', '/mp4hd')
            else:
                realM3u8 = realM3u8.replace('/m3u8', '/m3u8hd')

    finally:
        # 关闭浏览器
        await page.close()
        await browser.close()

    # videoinfo = VideoInfo()
    # videoinfo.title = title
    # videoinfo.author = author
    # videoinfo.scCount = scCount
    # videoinfo.realM3u8 = realM3u8
    # videoinfo.imgUrl = imgUrl
    print(realM3u8)
    return realM3u8


async def ini_browser():
    browser = await launch(headless=False, dumpio=True, devtools=False,
                           # userDataDir=r'F:\temporary',
                           args=[
                               # 关闭受控制提示：比如，Chrome正在受到自动测试软件的控制...
                               '--disable-infobars',
                               # 取消沙盒模式，沙盒模式下权限太小
                               '--no-sandbox',
                               '--ignore-certificate-errors',
                               '--disable-setuid-sandbox',
                               '--disable-features=TranslateUI',
                               '-–disable-gpu',
                               '--disable-software-rasterizer',
                               '--disable-dev-shm-usage',
                               # log 等级设置，如果出现一大堆warning，可以不使用默认的日志等级
                               '--log-level=3',
                           ])
    page = await browser.newPage()
    await page.setUserAgent(fake.user_agent())
    await page.setExtraHTTPHeaders(
        headers={'X-Forwarded-For': await util.genIpaddr(), 'Accept-Language': 'zh-cn,zh;q=0.5'})
    await page.evaluateOnNewDocument('() =>{ Object.defineProperties(navigator,'
                                     '{ webdriver:{ get: () => false } }) }')
    return browser, page


async def main():
    # 页数1-8
    for i in range(4, 9):
        url = 'https://91porn.com/v.php?next=watch&page=' + str(i)
        print(url)
        async with aiohttp.ClientSession() as session:
            async with session.get(url,
                                   proxy='http://127.0.0.1:10809',
                                   headers={
                                       'X-Forwarded-For': await util.genIpaddr(),
                                       'Accept-Language': 'zh-cn,zh;q=0.5',
                                       'User-Agent': fake.user_agent()
                                   }) as r:
                html = await r.text()
                # print(html)
                soup = BeautifulSoup(html, 'lxml')
                divs = soup.select('#wrapper > div.container.container-minheight > div.row > div > div > div > div')
                for div in divs:
                    # print(repr(div))
                    title = div.select('a > span')[0].text
                    author = div.select('span:nth-child(5)')[0].next_sibling.replace("\n", "").strip()
                    href = div.select('a')[0].get('href')
                    print(title, href, author)
                    # 开始爬取视频
                    realm3u8=await getVideoInfo91(href)
                    print(realm3u8)

asyncio.get_event_loop().run_until_complete(main())
