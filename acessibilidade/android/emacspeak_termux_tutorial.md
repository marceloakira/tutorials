# Emacspeak no Android (Termux) — Guia Completo e Funcional

Este tutorial descreve, passo a passo, como instalar e configurar o Emacspeak no Android usando Termux, incluindo todas as dependências necessárias para compilação e um servidor TTS customizado funcional baseado no termux-tts-speak.


## 📱 Visão Geral

- espeak → não funciona corretamente para áudio  
- backends tradicionais do Emacspeak → incompatíveis  
- termux-tts-speak → funciona (usa TTS nativo do Android)  
- solução final → speech server customizado em Tcl  


## 🧰 1. Instalação do Termux

⚠️ Não use a versão da Play Store  
Use a versão do F-Droid


## 🔌 2. Instalar Termux:API

Instale o app Termux:API no Android (via F-Droid)


## ⚙️ 3. Instalar dependências

```
pkg update
pkg upgrade

pkg install git emacs tcl termux-api make clang
```


## 🔍 4. Verificar ambiente

```
command -v tclsh
printf 'puts "teste tcl"\n' | tclsh
```


## 🔊 5. Testar TTS

```
termux-tts-speak "teste de voz"
```


## 📥 6. Instalar Emacspeak

```
git clone https://github.com/tvraman/emacspeak.git
cd emacspeak
make config
make
```


## 📁 7. Criar servidor TTS

Crie:

```
~/emacspeak/servers/termux-tts
```

Conteúdo:

```
#!/data/data/com.termux/files/usr/bin/tclsh

set logFile "/data/data/com.termux/files/home/termux-tts.log"
set pendingText ""
set lastSpoken ""

proc logmsg {msg} {
    global logFile
    set f [open $logFile a]
    puts $f $msg
    close $f
}

proc shell_quote {s} {
    regsub -all {'} $s {'"'"'} s
    return "'$s'"
}

proc normalize_text {text} {
    regsub -all {[[:space:]]+} [string trim $text] " " text
    return $text
}

proc stop_speech {} {
    catch {exec sh -c "termux-tts-speak --stop >/dev/null 2>&1"}
}

proc speak_now {text} {
    global lastSpoken
    set text [normalize_text $text]
    if {$text eq ""} { return }

    if {$text eq $lastSpoken} { return }

    set lastSpoken $text
    stop_speech
    after 80

    catch {exec sh -c "termux-tts-speak [shell_quote $text] >/dev/null 2>&1 &"}
}

proc queue_replace {text} {
    global pendingText
    set pendingText [normalize_text $text]
}

proc flush_pending {} {
    global pendingText
    if {$pendingText eq ""} { return }
    set text $pendingText
    set pendingText ""
    speak_now $text
}

while {![eof stdin]} {
    if {[gets stdin line] >= 0} {
        set line [string trim $line]

        if {[regexp {^q\s+"(.*)"$} $line -> text]} {
            queue_replace $text
        } elseif {[regexp {^q\s+(.*)$} $line -> text]} {
            queue_replace $text
        } elseif {$line eq "d"} {
            flush_pending
        } elseif {$line eq "s"} {
            set pendingText ""
            stop_speech
        }
    }
}
```

```
chmod +x ~/emacspeak/servers/termux-tts
```


## 🧠 8. Configurar Emacs

```
nano ~/.emacs.d/init.el
```

Adicionar:

```
(setq dtk-program "/data/data/com.termux/files/home/emacspeak/servers/termux-tts")
(load-file "/data/data/com.termux/files/home/emacspeak/lisp/emacspeak-setup.el")
```


## ▶️ 9. Executar

```
emacs
```


## 🔍 10. Logs

```
tail -n 100 ~/termux-tts.log
```


## 💡 11. Observações e Dicas de Uso

- Para usar o Emacs de forma produtiva no celular, é altamente recomendável utilizar um **teclado Bluetooth**, pois a interação por atalhos é essencial no Emacs.

- O servidor TTS apresentado neste tutorial é **experimental**. Ele foi desenvolvido como prova de conceito e pode conter:
  - bugs;
  - limitações de desempenho;
  - ausência de suporte completo ao protocolo do Emacspeak.

- Uma alternativa mais estável é utilizar um **servidor remoto via SSH**:
  - instale e configure o Emacspeak em uma máquina (Linux/macOS);
  - conecte-se a ela pelo Termux via SSH;
  - ao executar o Emacs, o áudio será reproduzido no celular, aproveitando o TTS do Android.


## 🚀 Conclusão

Emacspeak funcional no Android via Termux usando TTS nativo.
