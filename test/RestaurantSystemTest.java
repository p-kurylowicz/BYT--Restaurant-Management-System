import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Main test suite for the Restaurant Management System.
 *
 * <p>Aggregates all test classes covering:
 * <ul>
 *   <li>Basic attribute validation (strings, numbers, dates)</li>
 *   <li>Complex attributes (NutritionalInfo grouping)</li>
 *   <li>Multi-value attributes (collections)</li>
 *   <li>Static attributes (TAX_RATE, CANCELLATION_WINDOW_HOURS)</li>
 *   <li>Derived attributes (calculated values)</li>
 *   <li>Optional attributes (nullable fields)</li>
 *   <li>Class extent management</li>
 *   <li>Extent persistence (save/load to XML)</li>
 * </ul>
 */
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
