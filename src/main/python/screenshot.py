import requests
from bs4 import BeautifulSoup
from html2image import Html2Image

# Load css
styleTag = ""
with open("src/main/python/style.html", "r") as f:
    styleTag = f.read() + "\n"

url = "https://simresults.net/230714-3pK"
# req = requests.get(url)
req = ""
with open("src/main/python/results.html", "r") as f:
    req = f.read()
soup = BeautifulSoup(req, 'html.parser')

# Clear pesky ads
results = soup.find_all("div", {"class": ["sideblock-image", "midblock-image"]})
for result in results:
    result.decompose()

# Iterate through desired graphics, append css and screenshot it
ids = ["race1bestlaps", "race1consistency", "race1sectors", "race1positions-graph"]
hti = Html2Image(custom_flags=['--window-size=1100,1000', '--hide-scrollbars'])
for value, id in enumerate(ids):
    with open("temp.html", "w") as f:
        tag = soup.find(id=id)
        hti.screenshot(html_str=styleTag + str(tag.prettify()), save_as=f"out_{value}.png")
        f.write(styleTag + str(tag.prettify()))
