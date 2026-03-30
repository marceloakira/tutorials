#!/usr/bin/env bash
# Listener de áudio no macOS para o container Emacspeak.
#
# O container usa espeak-ng + socat para enviar WAV via TCP na porta 5500.
# Este script recebe o áudio e reproduz via CoreAudio usando sox.
#
# Pré-requisitos (instalar uma vez):
#   brew install sox socat
#
# Uso:
#   chmod +x start-speechd-macos.sh
#   ./start-speechd-macos.sh

set -euo pipefail

PORT=5500

check_deps() {
    local missing=()
    command -v sox   >/dev/null 2>&1 || missing+=("sox")
    command -v socat >/dev/null 2>&1 || missing+=("socat")
    if [[ ${#missing[@]} -gt 0 ]]; then
        echo "ERRO: dependências ausentes: ${missing[*]}"
        echo "Instale com: brew install ${missing[*]}"
        exit 1
    fi
}

check_deps

# Encerra instância anterior na mesma porta, se houver.
pkill -f "socat TCP-LISTEN:${PORT}" 2>/dev/null && sleep 1 || true

echo "==> Aguardando áudio do container na porta $PORT..."
echo "    Pressione Ctrl+C para parar."
echo ""
echo "    Para iniciar o container em outro terminal:"
echo "      docker compose -f compose.yaml -f compose.macos.yaml run --rm emacspeak"
echo ""

# Cada conexão TCP recebida do container inicia um processo sox que
# lê os dados WAV e reproduz via CoreAudio (saída padrão do macOS).
exec socat TCP-LISTEN:${PORT},reuseaddr,fork EXEC:"sox -t wav - -d"
