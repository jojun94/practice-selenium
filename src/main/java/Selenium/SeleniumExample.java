package Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JOJUN on 2020-10-30
 */
@SpringBootApplication
public class SeleniumExample {
    //WebDriver 설정
    private WebDriver driver;
    private WebElement element;
    private String url;
    private String serachKey="신림 전자담배";
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

        mapSearch();
    }

    public void mapSearch() {
        try {
            ArrayList<HashMap<String,String>> result = new ArrayList<>();

            driver.get(TEST_URL);
            Thread.sleep(5000);
            // 검색어 입력
            element = driver.findElement(By.className("input_search"));
            Thread.sleep(500);
            element.sendKeys(serachKey);
            element = driver.findElement(By.className("button_search"));
            // element.submit();
            element.sendKeys("\n");
            Thread.sleep(5000);
            // 서치프레임 탐색
            driver.switchTo().frame("searchIframe");
            List<WebElement> elements = driver.findElements(By.cssSelector("#_pcmap_list_scroll_container > ul > li"));

            getTitleAndPhone(elements, result);

            printResult(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }

    }

    public void getTitleAndPhone(List<WebElement> elements, ArrayList<HashMap<String,String>> result) throws InterruptedException {
        int count = 1 ; // 1 ~  w.size
        // 개별 항목 클릭 및 업체명 및 전화번호 가져오기
        for( WebElement w : elements ){
            // 개별 항목 클릭
            Thread.sleep(1000);
            w = driver.findElement(By.cssSelector("#_pcmap_list_scroll_container > ul > li:nth-child("+count+") > div > div"));
            count++;
            w.click();
            Thread.sleep(1000);
            driver.switchTo().defaultContent();
            driver.switchTo().frame("entryIframe");
            Thread.sleep(1000);

            //업체명 및 전화번호  TODO : 예외처리 전화번호가 없을 경우 ? 생각해보기
            HashMap<String,String> arr = new HashMap<>();
            WebElement t ;
            t= driver.findElement(By.cssSelector("#_title > span._3XamX"));
            arr.put("title",t.getText());
            t= driver.findElement(By.cssSelector("#app-root > div > div.place_detail_wrapper > div > div > div > div > ul > li > div > span"));
            arr.put("phone",t.getText());

            result.add(arr);

            driver.switchTo().defaultContent();
            driver.switchTo().frame("searchIframe");
            Thread.sleep(1000);
        }
    }

    public int totalPage(){
        int t = 1;

        return t;
    }

    public void printResult(ArrayList<HashMap<String,String>> result){

        System.out.println("Total Result Count : " + result.size());
        for(HashMap<String,String> hm : result){
            if(hm.containsKey("title")) System.out.print("Title : " + hm.get("title")+ " ,");
            if(hm.containsKey("phone"))System.out.print("Phone : " + hm.get("phone"));
            System.out.println();
        }
    }
}
