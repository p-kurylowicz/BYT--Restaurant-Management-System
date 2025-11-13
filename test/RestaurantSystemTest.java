import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Restaurant Management System")
@SelectClasses({
    BasicAttributesTest.class,
    ComplexAttributesTest.class,
    MultiValueAttributesTest.class,
    StaticAttributesTest.class,
    DerivedAttributesTest.class,
    OptionalAttributesTest.class,
    ClassExtentTest.class,
    ExtentPersistenceTest.class
})
public class RestaurantSystemTest {

}
