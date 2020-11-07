package tests;

import java.net.URL;
import java.util.Map;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

import org.json.simple.parser.ParseException;
import org.openqa.selenium.remote.DesiredCapabilities;


import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import resources.Constants;
import utils.PropertyReader;


import java.io.File;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

public class TestBase {
    protected WebDriverWait wait;

    protected AndroidDriver<AndroidElement> driver;
    DesiredCapabilities capabilities;

    public void setDesiredCapabilitiesForBrowserStack(Map<String, String> map) {

        map.entrySet().stream().
                forEach(e ->capabilities.setCapability(e.getKey().toString(), e.getValue().toString()) );


    }


    public AndroidDriver<AndroidElement> runOnBrowserStack(String deviceIndex) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("src/test/java/resources/browserstackparallel.conf.json"));
        JSONArray envs = (JSONArray) config.get("environments");


        Map<String, String> envCapabilities = (Map<String, String>) envs.get(Integer.parseInt(deviceIndex));
        setDesiredCapabilitiesForBrowserStack(envCapabilities);

        Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
        setDesiredCapabilitiesForBrowserStack(commonCapabilities);


        String username = System.getenv("BROWSERSTACK_USERNAME");
        if(username == null) {
            username = (String) config.get("username");
        }

        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if(accessKey == null) {
            accessKey = (String) config.get("access_key");
        }

        String app = System.getenv("BROWSERSTACK_APP_ID");
        if(app != null && !app.isEmpty()) {
            capabilities.setCapability("app", app);
        }

        driver = new AndroidDriver(new URL("http://"+username+":"+accessKey+"@"+config.get("server")+"/wd/hub"), capabilities);
        return driver;


    }

    public AndroidDriver<AndroidElement> runOnLocalEmulators(String udid, String systemPort) throws IOException {




        PropertyReader propertyReader = new PropertyReader();
        File f = new File("app");
        File fs = new File(f, propertyReader.getGlobalValue("APP_NAME"));

        capabilities.setCapability(MobileCapabilityType.APP, fs.getAbsolutePath());
        capabilities.setCapability("appActivity ", propertyReader.getGlobalValue("appActivity"));
        capabilities.setCapability(MobileCapabilityType.UDID, udid);
        capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, systemPort);
        //timeout // auto grant permisson

        driver = new AndroidDriver<AndroidElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;

    }



    @BeforeTest(alwaysRun = true)
    @Parameters({"udid", "systemPort" , "deviceIndex"})
    public void setUp(String udid , String systemPort , String deviceIndex) throws IOException, ParseException {


        if (Constants.env.equalsIgnoreCase("browserstack")){
            driver = runOnBrowserStack(deviceIndex);

        }

        else if (Constants.env.equalsIgnoreCase("local")){
            driver = runOnLocalEmulators(udid ,systemPort );
        }


    }

    @AfterTest(alwaysRun = true)
    public void tearDown(){
        driver.quit();
    }



}
