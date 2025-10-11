plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        toggleOffOn()
        ktfmt("0.58").kotlinlangStyle()
    }
}
