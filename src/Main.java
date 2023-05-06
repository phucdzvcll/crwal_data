import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import models.player_id;
import models.raw_name;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.devtools.v85.network.model.RequestId;
import org.openqa.selenium.devtools.v85.network.model.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class Main {
    static ObjectMapper mapper = new ObjectMapper();
    static TypeFactory typeFactory = mapper.getTypeFactory();
    static ArrayList<String> uIds = new ArrayList<>();

    static boolean isFirst = true;

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/Users/phuc/Downloads/chromedriver_mac_arm64/chromedriver");
        ChromeDriver driver = new ChromeDriver();
        DevTools devTools = driver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.addListener(Network.responseReceived(), responseReceived -> {

            Response response = responseReceived.getResponse();
            String url = response.getUrl();
            Integer status = response.getStatus();
            if (url.contains("https://en.fifaaddict.com/api2?q=fo4db&playername=") && status == 200) {
                try {
                    RequestId requestId = responseReceived.getRequestId();
                    String body = devTools.send(Network.getResponseBody(requestId)).getBody();
                    player_id participantJsonList = mapper.readValue(body, player_id.class);

                    FileWriter myWriter;
                    if (participantJsonList.db.isEmpty()) {
                        myWriter = new FileWriter("player_miss.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter);
                        PrintWriter pw = new PrintWriter(bw);
                        pw.println(url);
                        pw.close();
                        bw.close();
                    } else {
                        myWriter = new FileWriter("player_id.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter);
                        PrintWriter pw = new PrintWriter(bw);
                        participantJsonList.db.forEach(uid -> {
                            uIds.add(uid.uid);
                            pw.println(uid.uid);
                        });

                        if (isFirst) {
                            isFirst = false;
                            crawlPlayerInfo();
                        }

                        pw.close();
                        bw.close();
                    }
                    myWriter.close();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        FileWriter myWriter2 = new FileWriter("player_error.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter2);
                        PrintWriter pw = new PrintWriter(bw);
                        pw.println(url);
                        pw.close();
                        bw.close();
                        myWriter2.close();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }

        });
        ArrayList<raw_name> raw = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("/Users/phuc/Downloads/crwal data/src/player_fo4.json"));
            JSONArray jsonObject = (JSONArray) obj;
            String jsonString = jsonObject.toJSONString();
            raw = mapper.readValue(jsonString, typeFactory.constructCollectionType(ArrayList.class, raw_name.class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        driver.get("https://en.fifaaddict.com/");
        raw.forEach(rawName -> {
            try {
                FileWriter myWriter = new FileWriter("player_id.txt", true);
                BufferedWriter bw = new BufferedWriter(myWriter);
                PrintWriter pw = new PrintWriter(bw);
                pw.println("Player: " + rawName.name);
                pw.close();
                bw.close();
                myWriter.close();
                findPlayerByName(driver, rawName.name);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                try {
                    FileWriter myWriter2 = new FileWriter("player_error.txt", true);
                    System.out.println(e.getMessage());
                    BufferedWriter bw = new BufferedWriter(myWriter2);
                    PrintWriter pw = new PrintWriter(bw);
                    pw.println(rawName.name);
                    pw.close();
                    bw.close();
                    myWriter2.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }

        });

    }

    private static void crawlPlayerInfo() {
        ChromeDriver playerInfoPlayer = new ChromeDriver();
        DevTools devTools = playerInfoPlayer.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            Response response = responseReceived.getResponse();
            String url = response.getUrl();
            Integer status = response.getStatus();
            if (url.contains("https://en.fifaaddict.com/api2?fo4pid=") && status == 200) {
                try {
                    RequestId requestId = responseReceived.getRequestId();
                    String body = devTools.send(Network.getResponseBody(requestId)).getBody();

                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(body);
                    json.remove("dbrelate");

                    FileWriter myWriter;
                    if (json.isEmpty()) {
                        myWriter = new FileWriter("player_info_error.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter);
                        PrintWriter pw = new PrintWriter(bw);
                        pw.println(url);
                        pw.close();
                        bw.close();
                    } else {
                        myWriter = new FileWriter("player_info.json", true);
                        BufferedWriter bw = new BufferedWriter(myWriter);
                        PrintWriter pw = new PrintWriter(bw);
                        pw.println("," + json);
                        pw.close();
                        bw.close();
                    }
                    myWriter.close();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        FileWriter myWriter2 = new FileWriter("player_info_error.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter2);
                        PrintWriter pw = new PrintWriter(bw);
                        pw.println(url);
                        pw.close();
                        bw.close();
                        myWriter2.close();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }

        });
        int i = 0;
        boolean first = true;
        while (i < uIds.size()) {
            if (first) {
                first = false;
                playerInfoPlayer.get("https://en.fifaaddict.com/fo4db/pid" + uIds.get(i));
            } else {
                playerInfoPlayer.navigate().to("https://en.fifaaddict.com/fo4db/pid" + uIds.get(i));
            }
            i++;

        }
    }

    private static void findPlayerByName(ChromeDriver driver, String name) {
        driver.manage().deleteAllCookies();
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {

        }
        WebElement search_field = driver.findElement(By.id("fosPlayerName"));
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {

        }
        search_field.clear();
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {

        }
        search_field.sendKeys(name);
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {

        }
        WebElement btn_search = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/form/div[1]/div/button[1]"));
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {

        }
        btn_search.click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {

        }

    }

}
