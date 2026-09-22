# Espelha o emulador (ou celular) do MeuDia numa janela limpa com scrcpy,
# sem precisar abrir o Android Studio. Sobe o emulador se preciso, espera
# ficar pronto e abre o scrcpy com uma janela sempre-visível e leve.
#
# Uso:
#   .\scripts\live-preview.ps1
#   .\scripts\live-preview.ps1 -WindowX 1200 -WindowY 60
#
# Pré-requisitos: Android SDK (platform-tools + emulator) e scrcpy instalados.
# Se não estiverem no PATH, ajuste $AdbPath / $EmulatorPath / $ScrcpyPath abaixo.

param(
    [string]$AvdName = "MeuDia_Preview",
    [int]$WindowX = -1,
    [int]$WindowY = -1
)

$ErrorActionPreference = "Stop"

function Resolve-Tool {
    param([string]$Name, [string[]]$FallbackPaths)
    $cmd = Get-Command $Name -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    foreach ($p in $FallbackPaths) {
        if (Test-Path $p) { return $p }
    }
    throw "Não encontrei '$Name'. Instale ou ajuste o caminho no topo do script."
}

$AndroidHome = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }

$AdbPath = Resolve-Tool -Name "adb" -FallbackPaths @("$AndroidHome\platform-tools\adb.exe")
$EmulatorPath = Resolve-Tool -Name "emulator" -FallbackPaths @("$AndroidHome\emulator\emulator.exe")
$ScrcpyPath = Resolve-Tool -Name "scrcpy" -FallbackPaths @(
    (Get-ChildItem "$env:LOCALAPPDATA\Microsoft\WinGet\Packages\Genymobile.scrcpy*\scrcpy-win64-*\scrcpy.exe" -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName)
)

Write-Host "adb:      $AdbPath"
Write-Host "emulator: $EmulatorPath"
Write-Host "scrcpy:   $ScrcpyPath"

# 1) Sobe o emulador se ainda não houver device conectado.
$devices = & $AdbPath devices | Select-String "device$"
if (-not $devices) {
    Write-Host "Nenhum dispositivo conectado — iniciando o emulador '$AvdName' em segundo plano..."
    Start-Process -FilePath $EmulatorPath -ArgumentList "-avd", $AvdName, "-netdelay", "none", "-netspeed", "full" -WindowStyle Minimized
} else {
    Write-Host "Dispositivo já conectado, reaproveitando."
}

# 2) Espera o dispositivo responder e o boot terminar.
Write-Host "Aguardando o dispositivo ficar pronto..."
& $AdbPath wait-for-device
do {
    Start-Sleep -Seconds 2
    $boot = & $AdbPath shell getprop sys.boot_completed 2>$null
} while ($boot.Trim() -ne "1")
Write-Host "Dispositivo pronto."

# 3) Abre o scrcpy numa janela limpa, sempre por cima, sem áudio, leve.
$scrcpyArgs = @(
    "--window-title", "MeuDia",
    "--always-on-top",
    "--no-audio",
    "--max-size", "480"
)
if ($WindowX -ge 0) { $scrcpyArgs += @("--window-x", $WindowX) }
if ($WindowY -ge 0) { $scrcpyArgs += @("--window-y", $WindowY) }

Write-Host "Abrindo scrcpy..."
& $ScrcpyPath @scrcpyArgs
