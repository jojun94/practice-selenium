package Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by JOJUN on 2020-10-30
 */
@SpringBootApplication
public class SeleniumExample {
    //WebDriver 설정
    private WebDriver driver;
    private WebElement element;
    private String url;
    private String serachKey="강남 부동산";

    //Properties 설정
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static String WEB_DRIVER_PATH = "C:/chromedriver.exe";
    public static String TEST_URL = "https://map.naver.com/v5/";

    public static void main(String[] args) {
        SpringApplication.run(SeleniumExample.class);

        SeleniumExample test = new SeleniumExample();
    }

    public SeleniumExample() {
        //System Property SetUp
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        //Driver SetUp
        ChromeOptions options = new ChromeOptions();
        options.setCapability("ignoreProtectedModeSettings", true);
        driver = new ChromeDriver(options);
        search();
    }

    public void search() {
        try {
            driver.get(TEST_URL);
            Thread.sleep(5000);


            element = driver.findElement(By.className("input_search"));
            // 크롤링으로 text를 입력하면 굉장히 빠릅니다, 인식하지 못한 상태에서 이벤트를 발생시키면, 제대로 작동하지 않기 때문에 thread sleep으로 기다려줍니다.
            Thread.sleep(500);
            element.sendKeys(serachKey);


            //전송
            element = driver.findElement(By.className("button_search"));
//            element.submit();
            element.sendKeys("\n");

            Thread.sleep(10000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }

    }


}
