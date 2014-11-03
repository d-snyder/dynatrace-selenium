/**
 * Created by drsnyder on 10/16/14.
 */
import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;
import com.dynatrace.webautomation.DynaTraceWebDriver;
import com.dynatrace.webautomation.DynaTraceWebDriverHelper;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptExecutor;
import com.thoughtworks.selenium.webdriven.commands.WaitForPageToLoad;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

public class MyTest {

    protected static WebDriver driver = null;
    protected static DynaTraceWebDriverHelper dynaTrace = null;
    protected static RESTEndpoint endPoint = null;
    protected static String buildId = null;
    private String testRunId = null;

    //Set the timer name to match the test method name
    @Before
    public void beforeTest() {
        buildId = System.getProperty("BUILD_ID");
        String dt_username = System.getProperty("dt_username","admin");
        String dt_password = System.getProperty("dt_password","admin");
        endPoint = new RESTEndpoint(dt_username,dt_password,"http://localhost:8020");
        testRunId = endPoint.startTest("1","1","1","1",buildId,"1","browser",null,"uidriven","Windowsx86_64",buildId,new HashMap<String, String>());
        System.setProperty("testRunID",testRunId);

        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        driver = DynaTraceWebDriver.forWebDriver(new InternetExplorerDriver(ieCapabilities));
        dynaTrace = DynaTraceWebDriverHelper.forDriver(driver);

        dynaTrace.enableTimerNames(true);
        dynaTrace.enableMarkers(false);

        dynaTrace.setTimerName(testRunId);
    }

    //Set the timer name to match the test method name
    @After
    public void afterTest() {
        dynaTrace.setTimerName(null);
        endPoint.endTest(testRunId);

    }

    @AfterClass
    public static void tearDown() {
        if(driver != null) {
            driver.close();

            driver.quit();
        }
    }

    //Finally, the test.  Simply open the page www.dynatrace.com
    @Test
    public void openDynatraceDotCom()   {
        try {
            // Not sure if this is needed - Give the browser agent some time to connect.
            Thread.sleep(10000);

            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.navigate().to("http://dynatrace.com");
            driver.findElement(By.linkText("SITEMAP"));

            // To be safe, give the browser agent some time to send all it's data before IE is closed.
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
