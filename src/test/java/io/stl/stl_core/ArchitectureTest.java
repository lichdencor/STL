// package io.stl.stl_core;
//
// import com.tngtech.archunit.core.importer.ImportOption;
// import com.tngtech.archunit.junit.AnalyzeClasses;
// import com.tngtech.archunit.junit.ArchTest;
// import com.tngtech.archunit.lang.ArchRule;
//
// import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
// import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
//
// @AnalyzeClasses(packages = "io.stl.stl_core", importOptions =
// ImportOption.DoNotIncludeTests.class)
// public class ArchitectureTest {
//
// @ArchTest
// static final ArchRule layer_dependencies_are_respected =
// layeredArchitecture().consideringAllDependencies()
// .layer("Controller").definedBy("..controller..")
// .layer("Service").definedBy("..service..")
// .layer("Repository").definedBy("..repository..")
// .layer("Model").definedBy("..model..")
//
// .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
// .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
// .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
// .whereLayer("Model").mayOnlyBeAccessedByLayers("Repository", "Service",
// "Controller");
//
// @ArchTest
// static final ArchRule entities_must_reside_in_model_package = classes()
// .that().areAnnotatedWith(jakarta.persistence.Entity.class)
// .should().resideInAPackage("..model.entity..")
// .as("Entities should reside in a package '..model.entity..'");
//
// @ArchTest
// static final ArchRule repositories_must_reside_in_repository_package =
// classes()
// .that().haveNameMatching(".*Repository")
// .should().resideInAPackage("..repository..")
// .as("Repositories should reside in a package '..repository..'");
// }
