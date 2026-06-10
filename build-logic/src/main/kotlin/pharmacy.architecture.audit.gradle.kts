tasks.register("auditArchitecture") {
    group = "verification"
    description = "Audits inward-only module rules (A10/A17/A19/A20/A23) + DTO conventions (A24 @SerialName, A25 camelCase) + platform-folder ownership (A26 only :composeApp) + no expect/actual (A27 interfaces only) + typed errors (A28 no generic exceptions in production) + localized UI copy (A29 no Thai literals in production UI code)."

    val projectRoot = rootProject.projectDir
    val outputFile = layout.buildDirectory.file("reports/architecture-audit.txt")

    outputs.upToDateWhen { false }
    outputs.file(outputFile)

    doLast {
        val violations = mutableListOf<String>()

        fun grepFiles(label: String, dir: String, pattern: Regex) {
            val root = projectRoot.resolve(dir)
            if (!root.exists()) return
            root.walkTopDown()
                .filter { it.isFile && it.name.endsWith(".kt") && !it.absolutePath.contains("/build/") }
                .forEach { f ->
                    f.useLines { lines ->
                        lines.forEachIndexed { i, line ->
                            if (pattern.containsMatchIn(line)) {
                                violations += "$label  ${f.relativeTo(projectRoot)}:${i + 1}  ${line.trim()}"
                            }
                        }
                    }
                }
        }

        grepFiles(
            "A10 core->features      ",
            "core",
            Regex("""^\s*import\s+app\.devper\.pharm\.presentation\."""),
        )

        grepFiles(
            "A17 stale domain.common ",
            ".",
            Regex("""^\s*import\s+app\.devper\.pharm\.domain\.common\."""),
        )

        grepFiles(
            "A19 stale :core:ui pkg  ",
            ".",
            Regex(
                """^\s*import\s+app\.devper\.pharm\.presentation\.(""" +
                    """theme|designsystem|""" +
                    """common\.(BaseViewModel|BaseUiState|BaseFormViewModel|BaseFormUiState|RunVmTest)|""" +
                    """components\.(AppShell|ErrorBottomSheet|WindowSize)|""" +
                    """format\.|scanner\.|help\.MarkdownText|""" +
                    """print\.(ReceiptBuilder|buildReceiptTemplate)""" +
                    """)""",
            ),
        )

        grepFiles(
            "A20 features->data      ",
            "features",
            Regex("""^\s*import\s+app\.devper\.pharm\.data\."""),
        )

        grepFiles(
            "A23 feature DI non-VM   ",
            "features/src/commonMain/kotlin/app/devper/pharm/di",
            Regex("""^\s*import\s+app\.devper\.pharm\.domain\.(usecase|observer|parser)\."""),
        )

        fun scanDtoFields(label: String, dir: String) {
            val root = projectRoot.resolve(dir)
            if (!root.exists()) return
            root.walkTopDown()
                .filter { it.isFile && it.name.endsWith("Dto.kt") && !it.absolutePath.contains("/build/") }
                .forEach { f ->
                    var insideSerializable = false
                    var parenDepth = 0
                    f.useLines { lines ->
                        lines.forEachIndexed { i, line ->
                            val trimmed = line.trimStart()
                            if (trimmed.startsWith("@Serializable")) {
                                insideSerializable = true
                                return@forEachIndexed
                            }
                            if (insideSerializable) {
                                val opens = line.count { it == '(' }
                                val closes = line.count { it == ')' }
                                parenDepth += opens - closes
                                val isValLine = Regex("""^\s+val\s+[A-Za-z_]""").containsMatchIn(line)
                                if (isValLine && parenDepth > 0) {
                                    violations += "$label  ${f.relativeTo(projectRoot)}:${i + 1}  ${line.trim()}"
                                }
                                if (parenDepth <= 0) {
                                    insideSerializable = false
                                    parenDepth = 0
                                }
                            }
                        }
                    }
                }
        }

        scanDtoFields(
            "A24 DTO missing SerialName",
            "core/data/src/commonMain/kotlin/app/devper/pharm/data",
        )

        val snakeKotlinNameRe = Regex("""^\s*(?:@SerialName\([^)]*\)\s+)?val\s+([a-z][a-z0-9]*_[a-zA-Z0-9_]*)\s*:""")
        val dtoRoot = projectRoot.resolve("core/data/src/commonMain/kotlin/app/devper/pharm/data")
        if (dtoRoot.exists()) {
            dtoRoot.walkTopDown()
                .filter { it.isFile && it.name.endsWith("Dto.kt") && !it.absolutePath.contains("/build/") }
                .forEach { f ->
                    f.useLines { lines ->
                        lines.forEachIndexed { i, line ->
                            val m = snakeKotlinNameRe.find(line)
                            if (m != null) {
                                violations += "A25 DTO snake_case field    ${f.relativeTo(projectRoot)}:${i + 1}  ${line.trim()}"
                            }
                        }
                    }
                }
        }

        val platformOnlyForComposeApp = listOf("core/common", "core/domain", "core/ui", "core/data", "features")
        val platformDirRe = Regex("""/src/(androidMain|iosMain|iosArm64Main|iosSimulatorArm64Main|jvmMain|wasmJsMain|androidUnitTest|androidInstrumentedTest|iosTest|jvmTest|wasmJsTest)/""")
        platformOnlyForComposeApp.forEach { moduleDir ->
            val root = projectRoot.resolve(moduleDir)
            if (!root.exists()) return@forEach
            root.walkTopDown()
                .filter { it.isFile && it.name.endsWith(".kt") && !it.absolutePath.contains("/build/") }
                .filter { platformDirRe.containsMatchIn(it.absolutePath) }
                .forEach { f ->
                    violations += "A26 platform folder outside :composeApp    ${f.relativeTo(projectRoot)}"
                }
        }

        val expectRe = Regex("""^\s*(?:internal\s+|public\s+)?expect\s+(class|fun|val|var|object|interface|typealias)\s""")
        listOf("core", "features", "composeApp").forEach { topDir ->
            val root = projectRoot.resolve(topDir)
            if (!root.exists()) return@forEach
            root.walkTopDown()
                .filter { it.isFile && it.name.endsWith(".kt") && !it.absolutePath.contains("/build/") }
                .forEach { f ->
                    f.useLines { lines ->
                        lines.forEachIndexed { i, line ->
                            if (expectRe.containsMatchIn(line)) {
                                violations += "A27 expect declaration       ${f.relativeTo(projectRoot)}:${i + 1}  ${line.trim()}"
                            }
                        }
                    }
                }
        }

        val genericExceptionRe = Regex(
            """\b(throw|Result\.failure\()\s*(IllegalStateException|IllegalArgumentException|RuntimeException|Exception|UnsupportedOperationException|NullPointerException)\(""",
        )
        listOf("core", "features").forEach { topDir ->
            val root = projectRoot.resolve(topDir)
            if (!root.exists()) return@forEach
            root.walkTopDown()
                .filter { it.isFile && it.name.endsWith(".kt") && !it.absolutePath.contains("/build/") }
                .filter { f -> !f.absolutePath.let { it.contains("/commonTest/") || it.contains("/jvmTest/") || it.contains("/androidUnitTest/") } }
                .filter { f -> !f.absolutePath.contains("/features/test-fixtures/") }
                .forEach { f ->
                    f.useLines { lines ->
                        lines.forEachIndexed { i, line ->
                            if (genericExceptionRe.containsMatchIn(line)) {
                                violations += "A28 generic exception in production    ${f.relativeTo(projectRoot)}:${i + 1}  ${line.trim()}"
                            }
                        }
                    }
                }
        }


        val thaiLiteralRe = Regex("\"[^\"]*[\\u0E01-\\u0E3A\\u0E40-\\u0E5B][^\"]*\"")
        val previewMarkerRe = Regex("""@Preview|^\s*private (val|fun) (sample|preview)""")
        val a29AllowedFiles = setOf(
            "BulkImportJsonInput.kt",
            "DrugFormViewModel.kt",
            "DrugFormUiState.kt",
            "Ky12AddUiState.kt",
            "ImportFormViewModel.kt",
        )
        listOf("core/ui", "features", "composeApp").forEach { topDir ->
            val root = projectRoot.resolve(topDir)
            if (!root.exists()) return@forEach
            root.walkTopDown()
                .filter { it.isFile && it.name.endsWith(".kt") && !it.absolutePath.contains("/build/") }
                .filter { it.absolutePath.contains("/commonMain/") }
                .filter { !it.absolutePath.contains("/i18n/groups/") }
                .filter { !it.absolutePath.contains("/ui/print/") }
                .filter { !it.name.contains("Preview") }
                .filter { !it.absolutePath.contains("/features/test-fixtures/") }
                .filter { it.name !in a29AllowedFiles }
                .forEach { f ->
                    val lines = f.readLines()
                    val previewStart = lines.indexOfFirst { previewMarkerRe.containsMatchIn(it) }
                        .let { if (it < 0) lines.size else it }
                    for (i in 0 until previewStart) {
                        val line = lines[i]
                        if (line.contains(".contains(")) continue
                        if (thaiLiteralRe.containsMatchIn(line)) {
                            violations += "A29 Thai literal in production UI    ${f.relativeTo(projectRoot)}:${i + 1}  ${line.trim()}"
                        }
                    }
                }
        }

        val report = outputFile.get().asFile
        report.parentFile.mkdirs()
        if (violations.isEmpty()) {
            report.writeText("OK — no architecture violations.\n")
            logger.lifecycle("auditArchitecture: clean (0 violations)")
        } else {
            val body = violations.joinToString("\n")
            report.writeText(body + "\n")
            throw GradleException(
                "Architecture audit failed (${violations.size} violation${if (violations.size == 1) "" else "s"}):\n" +
                    body +
                    "\n\nFix the offending imports; re-run `./gradlew :composeApp:auditArchitecture`.",
            )
        }
    }
}

tasks.matching { it.name == "check" }.configureEach {
    dependsOn("auditArchitecture")
}
