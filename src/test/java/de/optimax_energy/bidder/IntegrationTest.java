package de.optimax_energy.bidder;

import de.optimax_energy.bidder.application.TestApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class IntegrationTest {

}
