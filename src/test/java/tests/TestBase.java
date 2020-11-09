package tests;

import java.net.URL;
import java.util.Iterator;
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

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import resources.Constants;
import utils.PropertyReader;


import java.io.File;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

public class TestBase {

    protected AndroidDriver<AndroidElement> driver;

    public DesiredCapabilities setCapability(Map<String, String> map , DesiredCapabilities capabilities){
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
        }
        return capabilities;


    }


    public AndroidDriver<AndroidElement> runOnBrowserStack(String deviceIndex) throws IOException, ParseException {

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("src/test/java/resources/browserstackparallel.conf.json"));
        JSONArray envs = (JSONArray) config.get("environments");
        DesiredCapabilities capabilities = new DesiredCapabilities();


        Map<String, String> envCapabilities = (Map<String, String>) envs.get(Integer.parseInt(deviceIndex));
        setCapability(envCapabilities ,capabilities );

        Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
        setCapability(commonCapabilities ,capabilities );
        capabilities.setCapability("app", System.getenv("BROWSERSTACK_APP_ID"));

        driver = new AndroidDriver(new URL("http://"+(String) config.get("username")+":"+(String) config.get("access_key")+"@"+config.get("server")+"/wd/hub"), capabilities);
        return driver;


    }

    public AndroidDriver<AndroidElement> runOnLocalEmulators(String udid, String SystemPort) throws IOException {

        PropertyReader propertyReader = new PropertyReader();
        File f = new File("app");
        File fs = new File(f, propertyReader.getGlobalValue("APP"));
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability(MobileCapabilityType.APP, fs.getAbsolutePath());
        capabilities.setCapability("appActivity ", propertyReader.getGlobalValue("appActivity"));
        capabilities.setCapability(MobileCapabilityType.UDID, udid);
        capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, SystemPort);


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
