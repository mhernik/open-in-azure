plugins {
    id("org.jetbrains.intellij.platform")
    kotlin("jvm") version "2.3.0"
}

group = "com.github.mhernik"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
}

dependencies {
    intellijPlatform {
        rider("2026.1") { useInstaller = false }
        bundledPlugin("Git4Idea")
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Open in Azure DevOps"
        version = "1.0.0"
        description = """
            Adds an "Open in Azure DevOps" action to the editor context menu and Git menu.
            Right-click any file or line of code to open it directly in your Azure DevOps
            repository in the browser, at the correct branch and line number.
        """.trimIndent()
        changeNotes = "Initial release."

        ideaVersion {
            sinceBuild = "261"  // Rider 2026.1
            untilBuild = provider { null } // no upper bound - stays compatible
        }

        vendor {
            name = "Marek Hernik"
            email = "marek.hernik@gmail.com"
        }
    }
}
