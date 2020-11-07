package tests;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ActivitiesPage;


public class BookProductCardTest extends TestBase {

    AndroidDriver<AndroidElement> driver;



    @Test(priority = 1)
    public void ProductCardTextTest()
    {
        ActivitiesPage activitiesPage = new ActivitiesPage(driver);
        activitiesPage.SetLocation();
        Assert.assertEquals(activitiesPage.getProductCard() , "Product Cards");

    }
    @Test(priority = 2)
    public void SeeAllButtonTextTest()
    {
        ActivitiesPage activitiesPage = new ActivitiesPage(driver);
        Assert.assertEquals(activitiesPage.getSeeAllText(), "SEE ALL");

    }

}
