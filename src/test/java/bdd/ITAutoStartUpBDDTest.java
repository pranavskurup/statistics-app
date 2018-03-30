package bdd;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by Pranav S Kurup on 3/29/2018.
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", tags = {"@startuptest", "@auto"})
public class ITAutoStartUpBDDTest {
}
