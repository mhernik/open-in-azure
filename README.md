# Open in Azure DevOps — JetBrains Plugin

Adds an **"Azure DevOps"** entry to the **Open In** context menu in Rider (and any JetBrains IDE).  
Right-click any file or line of code → **Open In → Azure DevOps** to open it in your browser at the correct branch and line number.

## Features

- Opens the current file in Azure DevOps at the **correct branch**
- Preserves the **current line number** when an editor is open
- Works from both the **editor** context menu and the **Project View**
- Auto-detects Azure DevOps remotes (`dev.azure.com` and legacy `visualstudio.com`)
- Action is **hidden** automatically for non-Azure repos (GitHub, GitLab, etc.)

## Requirements

- JetBrains Rider 2026.1+ (build 261+)
- Java 17+
- Gradle 8.8+

## Build

```bash
./gradlew buildPlugin
```

The `.zip` will be at `build/distributions/open-in-azure-devops-1.0.0.zip`.

## Install locally in Rider

1. Build the plugin (see above)
2. In Rider: `Settings → Plugins → ⚙ → Install Plugin from Disk…`
3. Select the `.zip` from `build/distributions/`
4. Restart Rider

## Develop & debug

```bash
./gradlew runIde
```

This launches a sandboxed Rider instance with the plugin loaded.

## Supported remote URL formats

| Format | Example |
|--------|---------|
| Modern | `https://dev.azure.com/myorg/myproject/_git/myrepo` |
| With user prefix | `https://myorg@dev.azure.com/myorg/myproject/_git/myrepo` |
| Legacy | `https://myorg.visualstudio.com/myproject/_git/myrepo` |

## Customisation

To change your display name or plugin ID, edit:
- `build.gradle.kts` → `vendor.name`, `group`
- `plugin.xml` → `<id>` and `<vendor>`
