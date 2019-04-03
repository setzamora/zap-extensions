rootProject.name = "zap-extensions"

val addOnsProjectName = "addOns"
include(addOnsProjectName)
include("testutils")

// Keep the add-ons in alphabetic order.
var addOns = listOf(
    "accessControl",
    "amf",
    "ascanrulesAlpha",
    "authstats",
    "birtreports",
    "browserView",
    "bugtracker",
    "callgraph",
    "cmss",
    "codedx",
    "cspscanner",
    "customreport",
    "domxss",
    "exportreport",
    "formhandler",
    "highlighter",
    "httpsInfo",
    "imagelocationscanner",
    "importLogFiles",
    "jsonview",
    "jxbrowsers",
    "jxbrowsers:jxbrowser",
    "jxbrowsers:jxbrowserlinux64",
    "jxbrowsers:jxbrowsermacos",
    "jxbrowsers:jxbrowserwindows",
    "jxbrowsers:jxbrowserwindows64",
    "openapi",
    "pscanrulesAlpha",
    "requester",
    "revisit",
    "saml",
    "sequence",
    "simpleexample",
    "soap",
    "sse",
    "tlsdebug",
    "viewstate",
    "vulncheck",
    "wappalyzer",
    "wavsepRpt"
)

addOns.forEach { include("$addOnsProjectName:$it") }

rootProject.children.forEach { project -> setUpProject(settingsDir, project) }

fun setUpProject(parentDir: File, project: ProjectDescriptor) {
    project.projectDir = File(parentDir, project.name)
    project.buildFileName = "${project.name}.gradle.kts"

    if (!project.projectDir.isDirectory) {
        throw AssertionError("Project ${project.name} has no directory: ${project.projectDir}")
    }
    if (!project.buildFile.isFile) {
        throw AssertionError("Project ${project.name} has no build file: ${project.buildFile}")
    }
    project.children.forEach { project -> setUpProject(project.parent!!.projectDir, project) }
}