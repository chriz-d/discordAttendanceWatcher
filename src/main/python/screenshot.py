import sys
import requests
from bs4 import BeautifulSoup
from html2image import Html2Image

def main():
    url = sys.argv[1]
    savePath = sys.argv[2]
    style = getCustomCss()
    content = getHtml(url)
    createScreenshot(content, style, savePath)

def getCustomCss():    
    # Load css
    styleTag = ""
    with open("src/main/python/style.html", "r") as f:
        styleTag = f.read() + "\n"
    return styleTag

def getHtml(url):
    req = downloadHtml(url)
    soup = BeautifulSoup(req, 'html.parser')
    cleanUpTags(soup)
    content = extractContent(soup)
    script = extractInlineScript(soup)
    for key in content:
        content[key] = content[key] + script
    return content

def downloadHtml(url):
    # Fetch HTML
    url = str(sys.argv[1])
    # req = requests.get(url)
    req = ""
    with open("src/main/python/results.html", "r") as f:
        req = f.read()
    return req

def extractContent(soup):
    ids = ["race1bestlaps", "race1consistency", "race1sectors", "race1positions-graph"]
    content = {}
    for id in ids:
        tag = soup.find(id=id)
        content[id] = str(tag.prettify()) 
    return content

def extractInlineScript(soup):
    script = soup.find_all("script")[8] # Nice magic number
    return str(script.prettify())

def cleanUpTags(soup): 
    # Clear pesky ads
    results = soup.find_all("div", {"class": ["sideblock-image", "midblock-image"]})
    for result in results:
        result.decompose()

def createScreenshot(content, style, path):
    # Iterate through desired graphics, append css and screenshot it
    hti = Html2Image(custom_flags=['--window-size=1100,1000', '--hide-scrollbars'], output_path=path)
    for value, key in enumerate(content):
        with open("temp.html", "w") as f:
            hti.screenshot(html_str= style + content[key], save_as=f"out_{value}.png")

if __name__ == "__main__":
    main()
