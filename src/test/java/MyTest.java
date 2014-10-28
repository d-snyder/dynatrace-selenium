/**
 * Created by drsnyder on 10/16/14.
 */
import com.dynatrace.webautomation.DynaTraceWebDriver;
import com.dynatrace.webautomation.DynaTraceWebDriverHelper;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.junit.Assert.assertNotNull;

public class MyTest {

    protected static WebDriver driver = null;
    protected static DynaTraceWebDriverHelper dynaTrace = null;


    // Makes the current test name available inside the test methods
    @Rule
    public TestName name;
    {
        name = new TestName();
    }


    // Setup of the IE Driver and Dyantrace wrapper classes
    @BeforeClass
    public static void startUp() {
        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        driver = DynaTraceWebDriver.forWebDriver(new InternetExplorerDriver(ieCapabilities));
        dynaTrace = DynaTraceWebDriverHelper.forDriver(driver);

        dynaTrace.enableTimerNames(false);
        dynaTrace.enableMarkers(false);
    }


    //Set the timer name to match the test method name
    @Before
    public void beforeTest() {
        dynaTrace.setTimerName(this.getClass().getName(), name.getMethodName());
    }


    //Be nice and close the driver
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
        dynaTrace.setTestStepName("openPage");
        driver.navigate().to("http://www.dynatrace.com");
    }
}
