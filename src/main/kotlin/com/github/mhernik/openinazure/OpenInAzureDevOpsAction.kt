package com.github.mhernik.openinazure

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.thisLogger
import git4idea.GitUtil
import git4idea.repo.GitRepository
import java.net.URLEncoder

class OpenInAzureDevOpsAction : AnAction() {

    private val logger = thisLogger()

    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (project == null || file == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        val repo = GitUtil.getRepositoryManager(project).getRepositoryForFileQuick(file)
        e.presentation.isEnabledAndVisible = repo != null && isAzureDevOpsRepo(repo)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)

        val repo = GitUtil.getRepositoryManager(project).getRepositoryForFileQuick(file)
            ?: run {
                logger.warn("No Git repository found for file: ${file.path}")
                return
            }

        val remoteUrl = getAzureRemoteUrl(repo) ?: run {
            logger.warn("No Azure DevOps remote found in repo: ${repo.root.path}")
            return
        }

        val repoRoot = repo.root.path
        val filePath = file.path
        if (!filePath.startsWith(repoRoot)) {
            logger.warn("File $filePath is not under repo root $repoRoot")
            return
        }
        val relativePath = filePath.removePrefix(repoRoot).trimStart('/', '\\')
            .replace('\\', '/')

        val lineNumber = editor?.caretModel?.logicalPosition?.line?.plus(1)

        val versionRef = buildVersionRef(repo)
        val url = buildAzureUrl(remoteUrl, versionRef, relativePath, lineNumber)
        logger.info("Opening Azure DevOps URL: $url")
        BrowserUtil.browse(url)
    }

    private fun isAzureDevOpsRepo(repo: GitRepository): Boolean =
        getAzureRemoteUrl(repo) != null

    private fun getAzureRemoteUrl(repo: GitRepository): String? =
        repo.remotes
            .flatMap { remote -> remote.urls }
            .firstOrNull { url ->
                url.contains("dev.azure.com", ignoreCase = true) ||
                url.contains("visualstudio.com", ignoreCase = true)
            }

    // GB = Git Branch, GC = Git Commit. Fall back to the current SHA when no
    // branch name is available (detached HEAD, in-progress rebase, etc.) so
    // the URL always resolves to something real.
    private fun buildVersionRef(repo: GitRepository): String {
        repo.currentBranchName?.let { return "GB${encode(it)}" }
        repo.currentRevision?.let { return "GC$it" }
        return "GBmain"
    }

    /**
     * Azure DevOps file URL:
     *   https://dev.azure.com/{org}/{project}/_git/{repo}?path=/{path}&version={GB|GC}{ref}[&line=N&lineEnd=N+1&lineStartColumn=1&lineEndColumn=1]
     */
    private fun buildAzureUrl(
        remoteUrl: String,
        versionRef: String,
        relativePath: String,
        lineNumber: Int?
    ): String {
        val cleanUrl = remoteUrl
            .replace(Regex("https://[^@]+@"), "https://")
            .trimEnd('/')

        val baseUrl = if (cleanUrl.contains("visualstudio.com")) {
            convertLegacyUrl(cleanUrl)
        } else {
            cleanUrl
        }

        val encodedPath = encodePath(relativePath)
        val sb = StringBuilder("$baseUrl?path=$encodedPath&version=$versionRef")

        if (lineNumber != null && lineNumber > 0) {
            val lineEnd = lineNumber + 1
            sb.append("&line=$lineNumber&lineEnd=$lineEnd&lineStartColumn=1&lineEndColumn=1")
        }

        return sb.toString()
    }

    /**
     * Converts https://{org}.visualstudio.com/{project}/_git/{repo}
     * to       https://dev.azure.com/{org}/{project}/_git/{repo}
     */
    private fun convertLegacyUrl(url: String): String {
        val regex = Regex("https://([^.]+)\\.visualstudio\\.com/(.+)")
        val match = regex.matchEntire(url) ?: return url
        val org = match.groupValues[1]
        val rest = match.groupValues[2]
        return "https://dev.azure.com/$org/$rest"
    }

    // Encode each segment, preserving '/' as the path separator.
    private fun encodePath(path: String): String {
        val normalized = if (path.startsWith("/")) path else "/$path"
        return normalized.split('/').joinToString("/") { encode(it) }
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, "UTF-8").replace("+", "%20")
}
