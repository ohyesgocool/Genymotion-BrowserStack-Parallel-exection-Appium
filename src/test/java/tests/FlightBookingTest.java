package tests;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.FlightsPage;


public class FlightBookingTest extends TestBase {

    AndroidDriver<AndroidElement> driver;




    @Test(priority = 1)
    public void selectFlightBookingCityTest(){
        FlightsPage flightsPage=new FlightsPage(driver);
        flightsPage.selectCityForFlight();
    }

    @Test(priority = 2)
    public void searchFlightButtonTest(){
        FlightsPage flightsPage=new FlightsPage(driver);
        Assert.assertEquals(flightsPage.searchFlight(),"SEARCH FLIGHTS");
    }

    @Test(priority = 3)
    public void sortPriceOnFlightListTest(){
        FlightsPage flightsPage=new FlightsPage(driver);
        flightsPage.sortPrice();

    }

}
