/*
 * Created on 25-May-2004
 */
package ca.uhn.hl7v2.protocol.impl.tests;

import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.frameworks;
import static org.ops4j.pax.exam.CoreOptions.knopflerfish;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.logProfile;
import static org.junit.Assert.*;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import ca.uhn.hl7v2.protocol.ApplicationRouter;
import ca.uhn.hl7v2.protocol.impl.AppRoutingDataImpl;
import ca.uhn.hl7v2.protocol.impl.ApplicationLoader;
import ca.uhn.hl7v2.protocol.impl.ApplicationRouterImpl;


/**
 * @author <a href="mailto:bryan.tripp@uhn.on.ca">Bryan Tripp</a>
 * @version $Revision: 1.2 $ updated on $Date: 2009-09-15 12:11:19 $ by $Author: jamesagnew $
 * @author Niranjan Sharma niranjan.sharma@med.ge.com This testcase has been
 *         extended for OSGI environment using Junit4 and PAX-Exam.
 */
@RunWith(JUnit4TestRunner.class)
public class ApplicationLoaderTest{

 // you get that because you "installed" a log profile in configuration.
    public Log logger = LogFactory.getLog(ApplicationLoaderTest.class);
    @Inject
    BundleContext bundleContext;
    
    @Configuration
    public static Option[] configure() {
	return options(frameworks(equinox(), felix(), knopflerfish())
		, logProfile()
		, systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO")
		, mavenBundle().groupId("org.ops4j.pax.url").artifactId("pax-url-mvn").version("0.4.0")
		, wrappedBundle(mavenBundle().groupId("org.ops4j.base").artifactId("ops4j-base-util").version("0.5.3"))
		, mavenBundle().groupId("ca.uhn.hapi").artifactId("hapi-osgi-base").version("1.0-beta1")
//		, vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5006" )


	);
    }
    
    @Test
    public void testLoad() throws Exception {
        ApplicationRouter router = new ApplicationRouterImpl();
        URL appResource = this.getClass().getClassLoader().getResource("ca/uhn/hl7v2/protocol/impl/tests/mock_apps");
        ApplicationLoader.loadApplications(router, appResource);
        
        AppRoutingDataImpl a = new AppRoutingDataImpl("ORU", "R01", "D", "2.4");
        AppRoutingDataImpl b = new AppRoutingDataImpl("ORU", "R01", "P", "2.2");
        AppRoutingDataImpl c = new AppRoutingDataImpl("ADT", "A01", "P", "2.1");
        AppRoutingDataImpl d = new AppRoutingDataImpl("ADT", "A04", "P", "2.1"); //not there
        assertTrue(router.hasActiveBinding(a));
        assertTrue(router.hasActiveBinding(b));
        assertTrue(router.hasActiveBinding(c));
        assertEquals(false, router.hasActiveBinding(d));
    }

}
