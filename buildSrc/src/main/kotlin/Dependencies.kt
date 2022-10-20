object Deps {
    val jupiter by lazy { "org.junit.jupiter:junit-jupiter:${Versions.jupiter}" }
    val lombok by lazy { "org.projectlombok:lombok:${Versions.lombok}" }
    val jabel by lazy { "com.github.bsideup.jabel:jabel-javac-plugin:${Versions.jabel}" }
    val annotations by lazy { "org.jetbrains:annotations:${Versions.annotations}" }
    val guava by lazy { "com.google.guava:guava:${Versions.guava}" }
    val asm by lazy { "org.ow2.asm:asm-debug-all:${Versions.asm}" }
}
