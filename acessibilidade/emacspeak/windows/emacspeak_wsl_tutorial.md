# 📘 Instalação do Emacs Acessível com Emacspeak no Windows (via WSL)

Este tutorial descreve como instalar e configurar o Emacspeak com Emacs TUI no Windows utilizando o WSL.

## 🎯 Visão geral

Windows → WSL (Ubuntu) → Emacs → Emacspeak → espeak-ng → áudio via WSLg

---

## 1. Instalar o WSL

No PowerShell (admin):

wsl --install

---

## 2. Instalar o Ubuntu

wsl --install -d Ubuntu

---

## 3. Verificar áudio

echo $WAYLAND_DISPLAY  
ls /mnt/wslg

---

## 4. Instalar dependências

sudo apt update

sudo apt install -y \
  emacs-nox git make gcc build-essential \
  tcl tclx tcl-dev \
  libasound2-plugins pulseaudio-utils alsa-utils sox \
  espeak-ng libespeak-ng-dev

---

## 5. Configurar áudio

echo 'export PULSE_SERVER=unix:/mnt/wslg/PulseServer' >> ~/.bashrc  
source ~/.bashrc

---

## 6. Testar áudio

paplay /usr/share/sounds/alsa/Front_Center.wav  
espeak-ng -v pt-br "teste de voz"

---

## 7. Clonar Emacspeak

cd /opt  
sudo git clone https://github.com/tvraman/emacspeak.git  
cd emacspeak  
git checkout 60.0

---

## 8. Compilar

make config  
make  
make espeak

---

## 9. Testar servidor

/opt/emacspeak/servers/espeak  

Ctrl + C para sair

---

## 10. Configurar init.el

~/.emacs.d/init.el

Conteúdo:

(setenv "PULSE_SERVER" "unix:/mnt/wslg/PulseServer")
(setenv "DTK_PROGRAM" "espeak")
(setenv "ESPEAKNG_VOICE" "pt-br")
(load-file "/opt/emacspeak/lisp/emacspeak-setup.el")

---

## 11. Executar

emacs -nw

---

## 🚀 Próximos passos

1. Otimizar para programação (Java, LaTeX)
2. Testar vozes e velocidade
3. Integrar com Piper ou Coqui TTS

---

## ✅ Conclusão

Ambiente acessível com Emacs + Emacspeak funcionando no Windows via WSL.
