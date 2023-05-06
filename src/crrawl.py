from selenium import webdriver
from selenium.webdriver import chrome
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time
from selenium.webdriver.common.by import By
from bs4 import BeautifulSoup
driver = webdriver.ChromeOptions()

driver.add_experimental_option("detach", True)
driver = webdriver.Chrome(options=driver, service=Service(ChromeDriverManager().install()))
url = "https://en.fifaaddict.com/fo4db/pidnznokpnz"
driver.get(url)

# search_field = driver.find_element(by=By.ID, value="fosPlayerName")
# time.sleep(0.5)
# search_field.send_keys("ronaldo")
# time.sleep(0.5)
# btn_search  = driver.find_element(by=By.XPATH, value="/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/form/div[1]/div/button[1]")
# time.sleep(0.5)
# btn_search.click()
# time.sleep(0.5)
# item_result  = driver.find_element(by=By.XPATH, value="/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[3]/table/tbody/tr[1]/td[2]/div/a")
# time.sleep(0.5)
# item_result.click()

time.sleep(0.5)
season = driver.find_element(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
print(season.text)
time.sleep(0.5)
skill = driver.find_element(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/div[2]/div[2]/span[6]/b')
ss = skill.get_attribute("class")
print(ss)
# skill = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/div[2]/div[2]/span[6]/b')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')
# season = driver.file_detector(by=By.XPATH, value = '/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/span')

