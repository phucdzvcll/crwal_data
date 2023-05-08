import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.player_id;
import models.InternalRequest;
import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.openqa.selenium.devtools.v85.network.model.RequestId;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static ObjectMapper mapper = new ObjectMapper();
    static ArrayList<String> uIds = new ArrayList<>();

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/Users/phuc/Downloads/chromedriver_mac_arm64/chromedriver");
        ChromeDriver driver = new ChromeDriver();
        DevTools devTools = driver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        ArrayList<InternalRequest> requestIds = new ArrayList<>();

        addListener(devTools, requestIds);

        List<String> raw = getListPlayer();


        driver.get("https://en.fifaaddict.com/");
        raw.forEach(rawName -> {
            try {
                findPlayerByName(driver, rawName);
            } catch (Exception e) {
                System.out.println("player_error");
                System.out.println(e.getMessage());
                try {
                    FileWriter myWriter2 = new FileWriter("player_error.txt", true);
                    BufferedWriter bw = new BufferedWriter(myWriter2);
                    PrintWriter pw = new PrintWriter(bw);
                    pw.println(rawName);
                    pw.close();
                    bw.close();
                    myWriter2.close();
                } catch (IOException ex) {
                    System.out.println(e.getMessage());
                }
            }
        });
        driver.close();
        crawlPlayerInfo();


    }

    private static List<String> getListPlayer() {
        List<String> raw = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get("/Users/phuc/Downloads/crwal data/player_unique.txt"))) {
            raw = lines.collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return raw;
    }

    private static void addListener(DevTools devTools, ArrayList<InternalRequest> requestIds) {
        devTools.addListener(Network.requestWillBeSent(), requestWillBeSent -> {
            String url = requestWillBeSent.getRequest().getUrl();
            if (url.contains("https://en.fifaaddict.com/api2?q=fo4db&playername=")) {
                RequestId requestId = requestWillBeSent.getRequestId();
                requestIds.add(new InternalRequest(requestId, url));
            }
        });
        devTools.addListener(Network.loadingFinished(), loadingFinished -> {
            RequestId requestId = loadingFinished.getRequestId();
            InternalRequest rq = null;
            for (InternalRequest request : requestIds) {
                if (request.getRequestId().toString().equals(requestId.toString())) {
                    rq = request;
                    break;
                }
            }
            if (rq != null) {
                try {
                    String body = devTools.send(Network.getResponseBody(rq.getRequestId())).getBody();
                    player_id participantJsonList = mapper.readValue(body, player_id.class);
                    FileWriter myWriter;
                    if (participantJsonList.db.isEmpty()) {
                        myWriter = new FileWriter("player_miss.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter);
                        PrintWriter pw = new PrintWriter(bw);
                        String decodedUrl = URLDecoder.decode(rq.getRequestUrl(), StandardCharsets.UTF_8);
                        URL parsedUrl = new URL(decodedUrl);
                        String playerName = URLDecoder.decode(parsedUrl.getQuery().split("&")[1].split("=")[1], StandardCharsets.UTF_8);
                        pw.println(playerName);
                        pw.close();
                        bw.close();
                        myWriter.close();
                    } else {
                        participantJsonList.db.forEach(uid -> {
                            uIds.add(uid.uid);
                        });
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        FileWriter myWriter2 = new FileWriter("player_error.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter2);
                        PrintWriter pw = new PrintWriter(bw);
                        String decodedUrl = URLDecoder.decode(rq.getRequestUrl(), StandardCharsets.UTF_8);
                        URL parsedUrl = new URL(decodedUrl);
                        String playerName = URLDecoder.decode(parsedUrl.getQuery().split("&")[1].split("=")[1], StandardCharsets.UTF_8);
                        pw.println(playerName);
                        pw.close();
                        bw.close();
                        myWriter2.close();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
    }

    private static void crawlPlayerInfo() {

        uIds.forEach(s -> {
            try {
                FileWriter myWriter2 = new FileWriter("player_id.txt", true);
                BufferedWriter bw = new BufferedWriter(myWriter2);
                PrintWriter pw = new PrintWriter(bw);
                pw.println(s);
                pw.close();
                bw.close();
                myWriter2.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        String uri = "mongodb://root:example@localhost:27017/";

        ConnectionString connectionString = new ConnectionString(uri);
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("player");
        MongoCollection<Document> collection = database.getCollection("player_info");
        ChromeDriver playerInfoPlayer = new ChromeDriver();
        DevTools devTools = playerInfoPlayer.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));


        ArrayList<InternalRequest> requestIds = new ArrayList<>();
        playerInfoAddListener(collection, devTools, requestIds);

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
            playerInfoPlayer.manage().deleteAllCookies();
        }
        playerInfoPlayer.close();
    }

    private static void playerInfoAddListener(MongoCollection<Document> collection, DevTools devTools, ArrayList<InternalRequest> requestIds) {
        devTools.addListener(Network.requestWillBeSent(), requestWillBeSent -> {
            String url = requestWillBeSent.getRequest().getUrl();
            if (url.contains("https://en.fifaaddict.com/api2?fo4pid=")) {
                requestIds.add(new InternalRequest(requestWillBeSent.getRequestId(), url));
            }
        });


        devTools.addListener(Network.loadingFinished(), loadingFinished -> {

            RequestId requestId = loadingFinished.getRequestId();
            InternalRequest rq = null;
            for (InternalRequest request : requestIds) {
                if (request.getRequestId().toString().equals(requestId.toString())) {
                    rq = request;
                    break;
                }
            }
            if (rq != null) {
                try {
                    String body = devTools.send(Network.getResponseBody(requestId)).getBody();
                    Document json = Document.parse(body);
                    if (json.isEmpty()) {
                        FileWriter myWriter = new FileWriter("player_info_error.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter);
                        PrintWriter pw = new PrintWriter(bw);
                        pw.println(rq.getRequestUrl());
                        pw.close();
                        bw.close();
                        myWriter.close();
                    } else {
                        json.remove("dbrelate");


                        Document existingDoc = collection.find(new Document("db.uid", json.get("db", Document.class).getString("uid"))).first();
                        if (existingDoc != null) {
                            System.out.println("Document with ID " + json.get("db", Document.class).getString("uid") + " already exists.");
                        } else {
                            collection.insertOne(json);
                            System.out.println("Inserted document with ID " + json.get("db", Document.class).getString("uid"));
                        }

                        collection.replaceOne(json, json);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        FileWriter myWriter2 = new FileWriter("player_info_error.txt", true);
                        BufferedWriter bw = new BufferedWriter(myWriter2);
                        PrintWriter pw = new PrintWriter(bw);
                        pw.println(rq.getRequestUrl());
                        pw.close();
                        bw.close();
                        myWriter2.close();
                    } catch (Exception ex) {
                        System.out.println(e.getMessage());
                    }
                }
            }

        });
    }

    private static void findPlayerByName(ChromeDriver driver, String name) {
        try {
            driver.manage().deleteAllCookies();
            WebElement search_field = driver.findElement(By.id("fosPlayerName"));
            search_field.clear();
            search_field.sendKeys(name);
            WebElement btn_search = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/div/div[2]/div/div[1]/form/div[1]/div/button[1]"));
            btn_search.click();
            Thread.sleep(500);
        } catch (InterruptedException ignored) {

        }
    }

}
