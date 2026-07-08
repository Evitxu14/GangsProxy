param([string]$WorkDir = "C:\Users\WinterOS\Desktop\GangsProxy-Build")

# Encode: use UTF-8 without BOM for Java files
$utf8NoBom = New-Object System.Text.UTF8Encoding $false

Write-Output "Applying GangsProxy branding to: $WorkDir"

# --- Phase 1: Replace string literals in .java files ---
$javaFiles = Get-ChildItem -Recurse -Filter "*.java" -Path "$WorkDir\src"
Write-Output "Processing $($javaFiles.Count) Java files..."

$pattern1 = '"ZenithProxy"'
$pattern2 = "'ZenithProxy'"
$pattern3 = 'ZenithProxy'
$replacement1 = '"Gang''sProxy"'
$replacement2 = "'Gang''sProxy'"
$replacement3 = 'Gang''sProxy'

foreach ($file in $javaFiles) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    $changed = $false
    
    # Replace exact string literal "ZenithProxy" -> "Gang'sProxy"
    if ($content -match $pattern1) {
        $content = $content -replace $pattern1, $replacement1
        $changed = $true
    }
    
    # Replace exact string literal 'ZenithProxy' -> 'Gang'sProxy'
    if ($content -match [regex]::Escape("'ZenithProxy'")) {
        $content = $content -replace [regex]::Escape("'ZenithProxy'"), $replacement2
        $changed = $true
    }
    
    # Replace GitHub URLs
    if ($content -match 'rfresh2/ZenithProxy') {
        $content = $content -replace 'rfresh2/ZenithProxy', 'Evitxu14/GangsProxy'
        $changed = $true
    }
    
    # Replace Discord invites
    if ($content -match 'discord\.gg/zenithproxy') {
        $content = $content -replace 'discord\.gg/zenithproxy', 'discord.gg/M5U8yfDbdw'
        $changed = $true
    }
    if ($content -match 'discord\.gg/2b2tvc') {
        $content = $content -replace 'discord\.gg/2b2tvc', 'discord.gg/M5U8yfDbdw'
        $changed = $true
    }
    
    # Replace "zenithproxy" in lowercase paths/URLs
    if ($content -match 'zenithproxy(?=[/\\"\''._ ])') {
        $content = $content -replace 'zenithproxy(?=[/\\"\''._ ])', 'gangsproxy'
        $changed = $true
    }
    
    if ($changed) {
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        Write-Output "  Updated: $($file.FullName.Replace($WorkDir, ''))"
    }
}

# --- Phase 2: Config and build files ---
Write-Output "`nUpdating build config files..."

# build.gradle.kts - fix Implementation-Title stays as "ZenithProxy" (GangsProxy kept this)
# But update rootProject.name
$buildFile = "$WorkDir\settings.gradle.kts"
$content = [System.IO.File]::ReadAllText($buildFile)
if ($content -match 'rootProject\.name\s*=\s*"ZenithProxy"') {
    $content = $content -replace 'rootProject\.name\s*=\s*"ZenithProxy"', 'rootProject.name = "GangsProxy"'
    [System.IO.File]::WriteAllText($buildFile, $content, $utf8NoBom)
    Write-Output "  Updated settings.gradle.kts"
}

# Update env vars in build.gradle.kts: ZENITH_DEV -> GANGS_DEV, ZENITH_RELEASE_TAG -> GANGS_RELEASE_TAG
$buildFile = "$WorkDir\build.gradle.kts"
$content = [System.IO.File]::ReadAllText($buildFile)
if ($content -match 'ZENITH_DEV') {
    $content = $content -replace 'ZENITH_DEV', 'GANGS_DEV'
}
if ($content -match 'ZENITH_RELEASE_TAG') {
    $content = $content -replace 'ZENITH_RELEASE_TAG', 'GANGS_RELEASE_TAG'
}
if ($content -match '"ZenithProxy"') {
    $content = $content -replace '"ZenithProxy"', '"GangsProxy"'
}
[System.IO.File]::WriteAllText($buildFile, $content, $utf8NoBom)
Write-Output "  Updated build.gradle.kts"

# --- Phase 3: Script files (.py, .bat, .sh) ---
Write-Output "`nUpdating script files..."
$scriptFiles = Get-ChildItem -Recurse -File -Path "$WorkDir\scripts", "$WorkDir\src\launcher" -ErrorAction SilentlyContinue
foreach ($file in $scriptFiles) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    $changed = $false
    
    if ($content -match 'ZenithProxy') {
        $content = $content -replace 'ZenithProxy', 'GangsProxy'
        $changed = $true
    }
    if ($content -match 'zenithproxy') {
        $content = $content -replace 'zenithproxy', 'gangsproxy'
        $changed = $true
    }
    if ($content -match 'ZENITH_') {
        $content = $content -replace 'ZENITH_', 'GANGS_'
        $changed = $true
    }
    if ($content -match 'rfresh2/ZenithProxy') {
        $content = $content -replace 'rfresh2/ZenithProxy', 'Evitxu14/GangsProxy'
        $changed = $true
    }
    
    if ($changed) {
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        Write-Output "  Updated: $($file.FullName.Replace($WorkDir, ''))"
    }
}

# --- Phase 4: Resources (icon, metadata) ---
Write-Output "`nUpdating resources..."
# Replace zenith_commit.txt, zenith_release.txt, zenith_mc_version.txt references
$resFiles = Get-ChildItem -Recurse -File -Path "$WorkDir\src\main\resources" -ErrorAction SilentlyContinue
foreach ($file in $resFiles) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    $changed = $false
    
    if ($content -match 'ZenithProxy') {
        $content = $content -replace 'ZenithProxy', 'GangsProxy'
        $changed = $true
    }
    if ($content -match 'zenithproxy') {
        $content = $content -replace 'zenithproxy', 'gangsproxy'
        $changed = $true
    }
    if ($content -match 'zenith\.png') {
        $content = $content -replace 'zenith\.png', 'gangs.png'
        $changed = $true
    }
    
    if ($changed) {
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        Write-Output "  Updated: $($file.FullName.Replace($WorkDir, ''))"
    }
}

# --- Phase 5: Rename resource files ---
$renamePairs = @(
    @{Old = "server-icon.png"; New = "server-icon.png"}  # Keep same name
)
# Rename any files with 'zenith' in name
Get-ChildItem -Recurse -File -Path "$WorkDir\src\main\resources" | Where-Object { $_.Name -match 'zenith' } | ForEach-Object {
    $newName = $_.Name -replace 'zenith', 'gangs'
    $newPath = Join-Path $_.Directory.FullName $newName
    if ($_.Name -ne $newName -and -not (Test-Path $newPath)) {
        Rename-Item -Path $_.FullName -NewName $newName
        Write-Output "  Renamed: $($_.Name) -> $newName"
    }
}

# --- Phase 6: Docs ---
Write-Output "`nUpdating docs..."
$docFiles = Get-ChildItem -Recurse -File -Path "$WorkDir\docs" -ErrorAction SilentlyContinue
foreach ($file in $docFiles) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    $changed = $false
    
    if ($content -match 'rfresh2/ZenithProxy') {
        $content = $content -replace 'rfresh2/ZenithProxy', 'Evitxu14/GangsProxy'
        $changed = $true
    }
    if ($content -match 'ZenithProxy' -and $file.Extension -in '.md', '.html', '.yml', '.yaml') {
        $content = $content -replace 'ZenithProxy', 'GangsProxy'
        $changed = $true
    }
    if ($content -match 'zenithproxy') {
        $content = $content -replace 'zenithproxy', 'gangsproxy'
        $changed = $true
    }
    
    if ($changed) {
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        Write-Output "  Updated: $($file.FullName.Replace($WorkDir, ''))"
    }
}

Write-Output "`n=== DONE ==="
