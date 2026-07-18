[CmdletBinding()]
param(
    [switch]$Clean
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$root = Split-Path -Parent $PSScriptRoot
$sourceRoot = Join-Path $root 'src'
$manifest = Join-Path $root 'META-INF/MANIFEST.MF'
$outputRoot = Join-Path $root 'bin'
$runRoot = Join-Path $outputRoot 'run'
$devRoot = Join-Path $outputRoot 'dev'

if($Clean) {
    if(Test-Path -LiteralPath $outputRoot) {
        Remove-Item -LiteralPath $outputRoot -Recurse -Force
    }
    Write-Host 'Clean complete.'
    exit 0
}

foreach($tool in @('javac', 'jar')) {
    if(-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
        throw "$tool was not found in PATH. Install JDK 17 or newer."
    }
}
if(-not (Test-Path -LiteralPath $manifest -PathType Leaf)) {
    throw "Common manifest not found: $manifest"
}

$probe = [Diagnostics.ProcessStartInfo]::new()
$probe.FileName = (Get-Command javac).Source
$probe.Arguments = '-version'
$probe.UseShellExecute = $false
$probe.RedirectStandardOutput = $true
$probe.RedirectStandardError = $true
$process = [Diagnostics.Process]::Start($probe)
$version = ($process.StandardOutput.ReadToEnd() + $process.StandardError.ReadToEnd()).Trim()
$process.WaitForExit()
if($version -notmatch 'javac\s+(?:1\.)?(\d+)' -or [int]$Matches[1] -lt 17) {
    throw "JDK 17 or newer is required; found: $version"
}

$modules = @(Get-ChildItem -LiteralPath $sourceRoot -Directory |
    Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'module-info.java') } |
    Sort-Object Name | ForEach-Object Name)
if($modules.Count -eq 0) {
    throw "No Java modules found under: $sourceRoot"
}
$moduleList = $modules -join ','

if(Test-Path -LiteralPath $outputRoot) {
    Remove-Item -LiteralPath $outputRoot -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $runRoot, $devRoot | Out-Null

Write-Host "Building runtime modules: $moduleList"
& javac -Xlint:all '-XDstringConcat=inline' --release 17 -encoding UTF-8 `
    -d $runRoot --module $moduleList --module-source-path $sourceRoot
if($LASTEXITCODE -ne 0) { throw 'Runtime compilation failed.' }

Write-Host "Building development modules: $moduleList"
& javac -g -Xlint:all '-XDstringConcat=inline' --release 17 -encoding UTF-8 `
    -d $devRoot --module $moduleList --module-source-path $sourceRoot
if($LASTEXITCODE -ne 0) { throw 'Development compilation failed.' }

foreach($module in $modules) {
    & jar cf0m (Join-Path $runRoot "$module.jar") $manifest -C (Join-Path $runRoot $module) .
    if($LASTEXITCODE -ne 0) { throw "Failed to package runtime module: $module" }

    $devModule = Join-Path $devRoot $module
    Copy-Item -LiteralPath (Join-Path $sourceRoot $module) -Destination (Join-Path $devModule 'src') -Recurse
    & jar cfm (Join-Path $devRoot "$module.jar") $manifest -C $devModule .
    if($LASTEXITCODE -ne 0) { throw "Failed to package development module: $module" }
}

Write-Host "Build complete: $outputRoot"
