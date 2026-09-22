#!/usr/bin/env bash
# Espelha o emulador (ou celular) do MeuDia numa janela limpa com scrcpy,
# sem precisar abrir o Android Studio. Sobe o emulador se preciso, espera
# ficar pronto e abre o scrcpy com uma janela sempre-visível e leve.
#
# Uso:
#   ./scripts/live-preview.sh
#   WINDOW_X=1200 WINDOW_Y=60 ./scripts/live-preview.sh
set -euo pipefail

AVD_NAME="${AVD_NAME:-MeuDia_Preview}"
ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
[ -d "$ANDROID_HOME" ] || ANDROID_HOME="$HOME/Android/Sdk"

install_scrcpy_if_missing() {
    if command -v scrcpy >/dev/null 2>&1; then
        return
    fi
    echo "scrcpy não encontrado — instalando..."
    case "$(uname -s)" in
        Darwin)
            command -v brew >/dev/null 2>&1 || { echo "Instale o Homebrew primeiro: https://brew.sh"; exit 1; }
            brew install scrcpy
            ;;
        Linux)
            if command -v apt >/dev/null 2>&1; then
                sudo apt update && sudo apt install -y scrcpy
            elif command -v pacman >/dev/null 2>&1; then
                sudo pacman -S --noconfirm scrcpy
            elif command -v dnf >/dev/null 2>&1; then
                sudo dnf install -y scrcpy
            else
                echo "Distribuição não reconhecida — instale o scrcpy manualmente: https://github.com/Genymobile/scrcpy"
                exit 1
            fi
            ;;
        *)
            echo "Sistema não suportado por este script (use live-preview.ps1 no Windows)."
            exit 1
            ;;
    esac
}

install_scrcpy_if_missing

ADB="$ANDROID_HOME/platform-tools/adb"
EMULATOR="$ANDROID_HOME/emulator/emulator"
command -v adb >/dev/null 2>&1 && ADB="adb"
command -v emulator >/dev/null 2>&1 && EMULATOR="emulator"

echo "adb:      $ADB"
echo "emulator: $EMULATOR"
echo "scrcpy:   $(command -v scrcpy)"

# 1) Sobe o emulador se ainda não houver device conectado.
if ! "$ADB" devices | grep -q "device$"; then
    echo "Nenhum dispositivo conectado — iniciando o emulador '$AVD_NAME' em segundo plano..."
    nohup "$EMULATOR" -avd "$AVD_NAME" -netdelay none -netspeed full > /tmp/meudia-emulator.log 2>&1 &
else
    echo "Dispositivo já conectado, reaproveitando."
fi

# 2) Espera o dispositivo responder e o boot terminar.
echo "Aguardando o dispositivo ficar pronto..."
"$ADB" wait-for-device
until [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
    sleep 2
done
echo "Dispositivo pronto."

# 3) Abre o scrcpy numa janela limpa, sempre por cima, sem áudio, leve.
args=(--window-title "MeuDia" --always-on-top --no-audio --max-size 480)
[ -n "${WINDOW_X:-}" ] && args+=(--window-x "$WINDOW_X")
[ -n "${WINDOW_Y:-}" ] && args+=(--window-y "$WINDOW_Y")

echo "Abrindo scrcpy..."
scrcpy "${args[@]}"
