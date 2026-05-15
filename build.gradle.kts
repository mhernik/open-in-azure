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
        intellijIdea("2026.1")
        bundledPlugin("Git4Idea")
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Open in Azure DevOps"
        version = "1.0.0"
        description = """
            <p>Adds an <b>Open in Azure DevOps</b> action to the editor, project view, and editor tab context menus.
            Right-click any file or line of code to open it directly in your Azure DevOps repository in the browser,
            at the correct branch and line number.</p>

            <h3>Features</h3>
            <ul>
              <li>Opens the current file in Azure DevOps at the <b>correct branch</b></li>
              <li>Preserves the <b>current line number</b> when invoked from the editor</li>
              <li>Works from the <b>editor</b>, <b>project view</b>, and <b>editor tab</b> context menus</li>
              <li>Auto-detects Azure DevOps remotes (<code>dev.azure.com</code> and legacy <code>visualstudio.com</code>)</li>
              <li>Action is hidden automatically for non-Azure repos (GitHub, GitLab, etc.)</li>
              <li>Handles detached HEAD by falling back to the current commit SHA</li>
            </ul>

            <h3>Supported remote URL formats</h3>
            <ul>
              <li><code>https://dev.azure.com/{org}/{project}/_git/{repo}</code></li>
              <li><code>https://{org}@dev.azure.com/{org}/{project}/_git/{repo}</code></li>
              <li><code>https://{org}.visualstudio.com/{project}/_git/{repo}</code></li>
            </ul>

            <p>Works in any JetBrains IDE with Git support (IntelliJ IDEA, Rider, PyCharm, WebStorm, PhpStorm, GoLand, RubyMine, CLion, and others).</p>
        """.trimIndent()
        changeNotes = """
            <h3>1.0.0</h3>
            <ul>
              <li>Initial release</li>
              <li>Open current file in Azure DevOps at the correct branch and line number</li>
              <li>Editor, project view, and editor tab context menu integration</li>
              <li>Support for modern (<code>dev.azure.com</code>) and legacy (<code>visualstudio.com</code>) remote URLs</li>
            </ul>
        """.trimIndent()

        ideaVersion {
            sinceBuild = "241"  // 2024.1 — uses only stable platform APIs
            untilBuild = provider { null } // no upper bound - stays compatible
        }

        vendor {
            name = "Marek Hernik"
            email = "marek.hernik@gmail.com"
        }
    }
}
