package es.asv.weather;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/** Enforces the boundaries described in the README, so breaking them fails the build instead of a review. */
@AnalyzeClasses(packages = "es.asv.weather", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule coreDoesNotDependOnFrameworksOrAdapters = noClasses()
            .that().resideInAnyPackage("..domain..", "..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "com.fasterxml..", "tools.jackson..", "..infrastructure..")
            .because("domain and use cases depend neither on Spring nor on AEMET");

    @ArchTest
    static final ArchRule domainDoesNotDependOnUseCases = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..application..");

    @ArchTest
    static final ArchRule sharedDoesNotDependOnFeatures = noClasses()
            .that().resideInAPackage("es.asv.weather.shared..")
            .should().dependOnClassesThat().resideInAnyPackage("es.asv.weather.municipality..", "es.asv.weather.forecast..");

    @ArchTest
    static final ArchRule featuresDoNotDependOnEachOther = slices()
            .matching("es.asv.weather.(*)..")
            .should().notDependOnEachOther()
            .ignoreDependency(alwaysTrue(), resideInAPackage("es.asv.weather.shared.."));
}
