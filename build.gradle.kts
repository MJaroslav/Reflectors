plugins {
    java
    `maven-publish`
}

repositories {
    mavenCentral()
}

group = "io.github.mjaroslav"
version = "0.5.0"
base.archivesName.set("Reflectors")

java {
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "16"
    options.release.set(8)
    options.encoding = "UTF-8"
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(16))
    })
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

dependencies {
    implementation(Deps.guava)
    implementation(Deps.asm)

    compileOnly(Deps.lombok)
    annotationProcessor(Deps.lombok)
    testCompileOnly(Deps.lombok)
    testAnnotationProcessor(Deps.lombok)

    annotationProcessor(Deps.jabel)
    testAnnotationProcessor(Deps.jabel)

    compileOnly(Deps.annotations)
    testCompileOnly(Deps.annotations)

    testImplementation(Deps.jupiter)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
