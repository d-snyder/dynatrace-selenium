Overview
========

This is a simple example of how to integrate the Dynatrace 6.0 browser agent with Selenium. It brings together information from a number of other examples, and hopefully addresses a few discrepancies.

Environment Setup
=================

Environment
-----------

The following environment was used to test this prototype:

 * Windows 7
 * IE 9.0
 * Dynatrace 6.0 server and collector installation on localhost (change pom file to point to your server).
 * Dynatrace IE native browser agent via windows installer ( [6.0 download link](https://downloads.compuwareapm.com/downloads/download.aspx?p=DT) )
 * Maven 3
 * easyTravel demo source code downloaded and unpacked to easyTravel directory.  See easyTravel/README.txt for location.
 
 
Install The Browser Agent
-------------------------

The browser agent is licensed like any other agent from Dynatrace. If your license does not include this, then please see the other guides out there for integrating Selenium with the free AJAX edition of Dynatrace. If you haven’t already, download and install the IE browser agent via the windows installer. For me, this only installed on the 32 bit version of IE, even though I am running a 64 bit version of Windows 7.  


Set Up System Profile
---------------------

Make sure there is a system profile configured on the Dynatrace server you are connecting to. The official documentation has a great walk-through section titled “[Browser Regression Analysis](https://community.compuwareapm.com/community/display/DOCDT60/Browser+Regression+Analysis#BrowserRegressionAnalysis-ExecuteAutomatedTests)”, so I won’t repeat that information.  Just make a note of the agent name you plan to use because you will need it for later configuration. One mistake I made was forgetting to check the “Browser” technology option in the general settings, so if you aren't seeing the browser sensor packs, check this setting.

IE Driver Server and Dynatrace Selenium Helpers
-----------------------------------------------

The IE Driver Server and Dynatrace Selenium helpers must be downloaded separately. There are inconsistent versions of these floating around, so the quickest way I have found to get these is to download the [Easy Travel demo application source code](https://community.compuwareapm.com/community/download/attachments/45383742/easyTravel-2.0.0.1534-src.zip?version=1&modificationDate=1409055458867&api=v2) that Dynatrace makes available. The pom file assumes the downloaded archive has been unzipped to the easyTravel directory. If you want to install these seperately, make sure you get the 32 bit version of the IE driver server, since the browser agent is installed to the 32 bit version of IE.


Test Class
=========

Junit is used to invoke Selenium. This code was pieced together mostly from the examples in the easyTravel store.



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


Running the Test
================

Maven POM
---------

For this prototype, Maven does the heavy lifting and the POM is fairly straightforward, except the browser agent requires a few environment variables set in order to automatically connect and start recording browser activity when the test starts.
This is accomplished by adding an "environmentVariables" setting to the surefire plugin configuration as follows:

     <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-surefire-plugin</artifactId>
       <configuration>
         <environmentVariables>
            <DT_AGENTNAME>${dt_agentname}</DT_AGENTNAME>
            <DT_SERVER>${dt_server}</DT_SERVER>
            <DT_CLEARCACHE>true</DT_CLEARCACHE>
            <DT_AGENTACTIVE>true</DT_AGENTACTIVE>
            <PATH>${basedir}/easyTravel/ThirdPartyLibraries/Selenium</PATH>
         </environmentVariables>
       </configuration>
     </plugin>
  
I have seen a number of different versions of these variables in various postings.  These are the ones that worked for me under dynatrace 6.0.  Note that the free AJAX edition uses different variables.
     
     
Executing Maven
---------------

Assuming Maven and Java are in the system path, the test can be run using the following command, which will compile all test sources and run the test invoking the IE Driver Server and IE in the process.  

    mvn test

If you need to override either the agent name or server information, it can be done as follows:
    
    mvn -Ddt_agentname=myagent -Ddt_server=myserver:myport test
    

Final Thoughts
==============

My goal here was to share what I learned in getting Selenium running with the Dynatrace browser agent. I recommend digging around in the Selenium directory of the Easy Travel demo for a much more extensive treatment.

Other Guides
------------

### Official Documentation

 [Dynatrace 6.0 Documentation: Diagnose Browser Activity](https://community.compuwareapm.com/community/display/DOCDT60/Diagnose+Browser+Activity)  
 [Dynatrace 6.0 Documentation: Browser Regression Analysis](https://community.compuwareapm.com/community/display/DOCDT60/Browser+Regression+Analysis#BrowserRegressionAnalysis)

### Community Links

[How to include dynaTrace in your Selenium Tests](https://community.compuwareapm.com/community/display/PUB/How+to+include+dynaTrace+in+your+Selenium+Tests)  
[How to use your Selenium Tests for automated JavaScript/AJAX Performance Analysis](http://apmblog.compuware.com/2010/05/21/how-to-use-your-selenium-tests-for-automated-javascriptajax-performance-analysis/)
