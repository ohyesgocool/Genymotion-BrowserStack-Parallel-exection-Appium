package tests;
import java.net.URL;
import java.util.Map;
import com.testvagrant.optimuscloud.dashboard.testng.OptimusCloudConstants;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import com.testvagrant.optimuscloud.entities.MobileDriverDetails;
import com.testvagrant.optimuscloud.remote.OptimusCloudDriver;
import com.testvagrant.optimuscloud.remote.OptimusCloudManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import resources.Constants;
import utils.JSONMapper;
import utils.PropertyReader;
import utils.SetCapability;


import java.io.File;

import java.io.IOException;

public class TestBase {

    protected AndroidDriver<AndroidElement> driver;
    protected MobileDriverDetails mobileDriverDetails;
    private OptimusCloudManager optimusCloudManager;

    SetCapability setCapability = new SetCapability();
    JSONMapper jsonMapper = new JSONMapper();


    public AndroidDriver<AndroidElement> runOnBrowserStack(String deviceIndex) throws IOException, ParseException {

        JSONObject config = jsonMapper.getJSONConfig("src/test/java/resources/browserstackparallel.conf.json");
        JSONArray envs = (JSONArray) config.get("environments");
        DesiredCapabilities capabilities = new DesiredCapabilities();

        Map<String, String> envCapabilities = (Map<String, String>) envs.get(Integer.parseInt(deviceIndex));
        setCapability.mapCapability(envCapabilities ,capabilities );

        Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
        setCapability.mapCapability(commonCapabilities ,capabilities );
        capabilities.setCapability("app", System.getenv("BROWSERSTACK_APP_ID"));

        driver = new AndroidDriver(new URL("http://"+(String) config.get("username")+":"+(String) config.get("access_key")+"@"+config.get("server")+"/wd/hub"), capabilities);
        return driver;


    }

    public AndroidDriver<AndroidElement> localFromConfig(String deviceIndex ) throws IOException, ParseException {

        PropertyReader propertyReader = new PropertyReader();
        File f = new File("app");
        File fs = new File(f, propertyReader.getGlobalValue("APP"));
        JSONObject config = jsonMapper.getJSONConfig("src/test/java/resources/browserstackparallel.conf.json");
        JSONArray envs = (JSONArray) config.get("environments");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Map<String, String> envCapabilities = (Map<String, String>) envs.get(Integer.parseInt(deviceIndex));
        setCapability.mapCapability(envCapabilities ,capabilities );
        capabilities.setCapability(MobileCapabilityType.APP, fs.getAbsolutePath());
        optimusCloudManager = new OptimusCloudManager();
        mobileDriverDetails = new OptimusCloudDriver().createDriver(capabilities);
        //iTestContext.setAttribute(OptimusCloudConstants.MOBILE_DRIVER, mobileDriverDetails);
        driver = (AndroidDriver<AndroidElement>) mobileDriverDetails.getMobileDriver();
        driver = new AndroidDriver(new URL("http://"+(String) config.get("server")+"/wd/hub"), capabilities);
        return driver;

    }


    @BeforeTest(alwaysRun = true)
    @Parameters({"udid", "systemPort" , "deviceIndex"})
    public void setUp(String udid , String systemPort , String deviceIndex) throws IOException, ParseException {

    if (Constants.env.equalsIgnoreCase("browserstack")){
            driver = runOnBrowserStack(deviceIndex);

      }

        else if (Constants.env.equalsIgnoreCase("local")){
            driver = localFromConfig(deviceIndex);
        }


    }

    @AfterTest(alwaysRun = true)
    public void tearDown(){
        driver.quit();
    }

}
