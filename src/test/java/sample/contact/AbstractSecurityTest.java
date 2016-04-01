package sample.contact;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-security.xml",
		"/applicationContext-common-authorization.xml",
		"/applicationContext-common-business.xml" })
public abstract class AbstractSecurityTest {
	
}
