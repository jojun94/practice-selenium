package Selenium;

import org.openqa.selenium.*;
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
    private String serachKey="신림 전자담배";

    private static int SLEEP_TIME = 1000;
    //Properties 설정
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static String WEB_DRIVER_PATH = "C:/chromedriver.exe";
    public static String URL = "https://map.naver.com/v5/";
    /*
    * 네이버 지도 검색 후 셀레니움을 이용하여 Title과 Phone을 가져와
    * Excel File로 Export 하는 Toy Prject
    * Page per 50
    * 6 Page
    * SLEEP_TIME 1000 ms
    * takes 20 min
    * TODO : Excel Export, 주소 추가,
    *  EntityIframe 로딩이 먹통이 되어 Saving 할 수 없는 상황 발생 -> status code로 판별?
    */
    public static void main(String[] args) {
        SpringApplication.run(SeleniumExample.class);

        SeleniumExample test = new SeleniumExample();

        return;
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
    //네이버 지도 검색어 입력 후 검색, 서치프레임으로 driver 이동
    public void init() throws InterruptedException {
        driver.get(URL);
        Thread.sleep(SLEEP_TIME * 5);
        // 검색어 입력
        element = driver.findElement(By.className("input_search"));
        Thread.sleep(SLEEP_TIME / 2);
        element.sendKeys(serachKey);
        element = driver.findElement(By.className("button_search"));
        // element.submit();
        element.sendKeys("\n");
        Thread.sleep(SLEEP_TIME * 5);

        // 서치프레임 탐색
        driver.switchTo().frame("searchIframe");
    }
    public void mapSearch() {
        int total = 0;
        try {
            ArrayList<HashMap<String,String>> result = new ArrayList<>();

            init();

            // Reload , 1페이지로 돌리기 위해
            total = totalPage();
            System.out.println("Total Page : "+total);

            init();

            driver.manage().window().maximize();

            //스크롤다운 : 처음 탐색시 스크롤 -> 모든 엔티티를 가져오기 위해서
            scrollDown(driver);
            // TODO : 처음 탐색 시 검색 결과가 존재하지 않을 경우 , 50개 중에 10개만 가져옴. -> 스크롤이 문제 (해결)
//            List<WebElement> elements = driver.findElements(By.cssSelector("#_pcmap_list_scroll_container > ul > li"));
            List<WebElement> elements ;
            for(int i = 0 ; i< total ; i++){
                elements = driver.findElements(By.cssSelector("#_pcmap_list_scroll_container > ul > li"));
                if(i == 0 ) { // 처음 검색 -> 다음페이지
                    getTitleAndPhone(elements, result);
                    clickNextPage();
                }
                else if(i == total -1 ) getTitleAndPhone(elements, result); // 마지막 페이지는 다음 X
                else{ // 검색 -> 다음 페이지
                    getTitleAndPhone(elements, result);
                    clickNextPage();
                }
            }

            System.out.println("############### OutPut ############");
            printResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }

    }
    // Title 과 Phone 을 가져옴
    public void getTitleAndPhone(List<WebElement> elements, ArrayList<HashMap<String,String>> result) throws InterruptedException {
//        int count = 1 ; // 1 ~  page contents
        int max = elements.size();
        System.out.println("Will get : "+max);
        WebElement w;
        // 개별 항목 클릭 및 업체명 및 전화번호 가져오기
        for( int count = 1 ; count <= max ; count++ ){
//            if(count >= max) break;
            // 개별 항목 클릭
            Thread.sleep(SLEEP_TIME);
            w = driver.findElement(By.cssSelector("#_pcmap_list_scroll_container > ul > li:nth-child("+ count +") > div > div"));
//            count++;
            w.click();
            Thread.sleep(SLEEP_TIME);
            driver.switchTo().defaultContent();
            driver.switchTo().frame("entryIframe");
            Thread.sleep((int) (SLEEP_TIME * 1.5));

            //업체명 및 전화번호  TODO : 예외처리 전화번호가 없을 경우 ? cssSelector로 인해 주소가 들어간다. (해결)  주소도 필요할까 ? 전화번호가 없고 주소만 있을 경우는 ?
            HashMap<String,String> arr = new HashMap<>();
            WebElement t ;
            t= driver.findElement(By.cssSelector("#_title > span._3XamX"));
            arr.put("title",t.getText());
            t= driver.findElement(By.cssSelector("#app-root > div > div.place_detail_wrapper > div > div > div > div > ul > li > div > span"));
            arr.put("phone",t.getText());
            /* TODO : 주소 추가 부분
            t= driver.findElement(By.cssSelector("#app-root > div > div.place_detail_wrapper > div > div > div > div > ul > li > div > span"));
            arr.put("address",t.getText());
            */
            result.add(arr);

            driver.switchTo().defaultContent();
            driver.switchTo().frame("searchIframe");

        }
        System.out.println("########## PROGRESS #########");
        printResult(result);
    }
    // 전체 페이지 갯수 탐색
    public int totalPage(){
        int t =1;
        boolean isLast =false;
        String s = "";
        String lastFlag = "_3-ZeE";

            List<WebElement> es;
            try{
                es = driver.findElements(By.cssSelector("#app-root > div > div > div > a"));

                //페이지가 5개 이하일 때
                if( es.size() < 7) return t = es.size() - 2;
                else{
                    // 페이지가 5개 이상일 때
                    while(!isLast){
                        es = driver.findElements(By.cssSelector("#app-root > div > div > div > a"));
                        WebElement e = es.get(es.size()-1);
                        s=e.getAttribute("class");
                        e.click();
                        Thread.sleep(SLEEP_TIME);
                        // lastFlag : 페이징의 마지막을 뜻하는 클래스를 포함하고 있으면 Return
                        if(s.contains(lastFlag)) {
                            return t;
                        }
                        t ++ ;
                    }
                }
            }catch (Exception err){
                return 0;
            }
        return t;
    }
    // 콘솔출력 TODO : 엑셀로 Export 하기
    public void printResult(ArrayList<HashMap<String,String>> result){
        System.out.println("Total Result Count : " + result.size());
        for(HashMap<String,String> hm : result){
            if(hm.containsKey("title")) System.out.print("Title : " + hm.get("title")+ " ,");
//            if(hm.containsKey("phone")) System.out.print("Phone : " + hm.get("phone")+ " ,");
            if(hm.containsKey("phone")) System.out.print("Phone : " + hm.get("phone"));
/*   TODO: address 추가    if(hm.containsKey("address")) System.out.print("Address : " + hm.get("address"));*/
            System.out.println();
        }
    }
    // Next Page Click
    public void clickNextPage() throws InterruptedException {
        List<WebElement> es = driver.findElements(By.cssSelector("#app-root > div > div > div > a")) ;
        WebElement e = es.get(es.size()-1);
        e.click();
        Thread.sleep(SLEEP_TIME);

        scrollDown(driver);
    }
    // 전체 Entity를 가져오기 위해 스크롤 serachIframe Scroll Down
    public void scrollDown(WebDriver driver) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int count = 5;
        int num  = 0;

        while(count > 0){
            List<WebElement> es = driver.findElements(By.cssSelector("#_pcmap_list_scroll_container > ul > li"));
            num = es.size();
            WebElement e = driver.findElement(By.cssSelector("#_pcmap_list_scroll_container > ul > li:nth-child("+num+")"));
            js.executeScript("arguments[0].scrollIntoView(true);", e);
            Thread.sleep(SLEEP_TIME / 2);
            count --;
        }
        System.out.println("Content per Page : "+ num);
    }


}
