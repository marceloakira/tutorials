FROM debian:trixie-slim

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV DTK_PROGRAM=espeak
# Diretório persistente para o cache de native compilation do Emacs (.eln)
ENV EMACS_ELN_CACHE=/root/.emacs.d/eln-cache

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    git \
    bash \
    make \
    gcc \
    g++ \
    libc6-dev \
    tcl \
    tcl8.6-dev \
    tclx8.4 \
    emacs-nox \
    espeak-ng \
    espeak-ng-data \
    libespeak-ng-dev \
    libespeak-ng-libespeak-dev \
    file \
    procps \
    curl \
    libcurl4 \
    libcurl4-openssl-dev \
    cmake \
    socat \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /opt

RUN git clone https://github.com/tvraman/emacspeak.git

RUN cd /opt/emacspeak \
 && git describe --tags || true \
 && make config \
 && make -j"$(nproc)"

RUN cd /opt/emacspeak/servers/native-espeak \
 && make clean || true \
 && make

# Servidor TTS espeak-socat: implementa o protocolo nativo do emacspeak (Tcl)
# usando espeak-ng --stdout | socat para enviar WAV via TCP ao host macOS.
# Não depende de ALSA, PulseAudio, speech-dispatcher nem e2spd.
RUN cat > /opt/emacspeak/servers/espeak-socat <<'EOF'
#!/usr/bin/tclsh
package require Tclx
set servers /opt/emacspeak/servers
source $servers/tts-lib.tcl

# --------------- backend TTS ---------------
proc synth {text} {
    global tts
    set tmpfile [exec mktemp /tmp/espk.XXXXXX]
    catch {
        set fh [open $tmpfile w]
        fconfigure $fh -encoding utf-8
        puts $fh $text
        close $fh
        exec espeak-ng -s $tts(speech_rate) -f $tmpfile --stdout \
             | socat - TCP4:host.docker.internal:5500
    }
    catch {file delete -force $tmpfile}
    return ""
}

proc stop {} {
    catch {exec pkill -f {espeak-ng.*--stdout}}
    return ""
}

proc speakingP {} { return 0 }

proc service {} {
    update idletasks
    return 0
}

proc setRate {ignore rate} {
    global tts
    set tts(speech_rate) [expr {int($rate)}]
    return ""
}

# --------------- protocolo emacspeak ---------------
proc tts_say {text} {
    synth "$text "
    return ""
}

proc l {text} {
    synth "$text "
    return ""
}

proc d {} { speech_task }

proc s {} {
    stop
    queue_clear
    return ""
}

proc t  {{pitch 440} {duration 50}} { return "" }
proc sh {{duration 50}} { return "" }

proc tts_set_speech_rate {rate} {
    global tts
    set tts(speech_rate) $rate
    return ""
}

proc tts_set_punctuations {mode} {
    global tts
    set tts(punctuations) $mode
    return ""
}

proc tts_set_character_scale {factor} {
    global tts
    set tts(char_factor) $factor
    return ""
}

proc tts_split_caps {flag} {
    global tts
    set tts(split_caps) $flag
    return ""
}

proc r {rate} {
    global queue tts
    set queue($tts(q_tail)) [list r $rate]
    incr tts(q_tail)
    return ""
}

proc tts_reset {} {
    stop
    queue_clear
    return ""
}

proc clean {element} {
    return $element
}

proc speech_task {} {
    global queue tts
    set tts(talking?) 1
    set length [queue_length]
    loop index 0 $length {
        set event [queue_remove]
        set event_type [lindex $event 0]
        switch -exact -- $event_type {
            s {
                synth "[clean [lindex $event 1]] "
            }
            c {
                synth "[lindex $event 1] "
            }
            a {
                catch {exec $tts(play) [lindex $event 1] >/dev/null &}
            }
            b {
                if {$tts(beep)} { eval beep [lreplace $event 0 0] }
            }
            r {
                set tts(speech_rate) [lindex $event 1]
            }
        }
        if {$tts(talking?) == 0} break
    }
    set tts(talking?) 0
    return ""
}

# --------------- inicialização ---------------
fconfigure stdin -encoding utf-8
tts_initialize
set tts(speech_rate) 225
set tts(char_factor) 1.0
set tts(play) "true"
commandloop
EOF
RUN chmod +x /opt/emacspeak/servers/espeak-socat

# Pré-compila nativamente os arquivos do emacspeak durante o build.
# A compilação é síncrona (native-compile, não native-compile-async), portanto
# ocorre no processo atual onde as variáveis já estão definidas — sem workers
# assíncronos com ambiente incompleto. Os .eln são gravados em /opt/emacspeak/eln/
# (fora do volume Docker), garantindo que estejam presentes em qualquer container.
RUN cat > /tmp/precompile-emacspeak.el <<'ELISP'
(add-to-list 'load-path "/opt/emacspeak/lisp")
;; Define as variáveis que emacspeak-preamble.el usa em eval-when-compile.
;; defconst do próprio preamble as redefine depois com o valor correto.
(defvar emacspeak-xslt-directory "/opt/emacspeak/xsl")
(defvar emacspeak-sounds-dir     "/opt/emacspeak/sounds/")
;; Direciona a saída .eln para /opt/emacspeak/eln/ (Emacs 28 e 29+).
(make-directory "/opt/emacspeak/eln" t)
(dolist (sym '(comp-eln-load-path native-comp-eln-load-path))
  (when (boundp sym)
    (set sym (list "/opt/emacspeak/eln/"))))
;; Compila cada .el sincronamente; erros individuais não abortam o loop.
(when (fboundp 'native-compile)
  (dolist (f (directory-files "/opt/emacspeak/lisp/" t))
    (when (and (string-match-p "\\.el$" f) (file-regular-p f))
      (condition-case err
          (native-compile f)
        (error (message "Skipped %s: %s" f (error-message-string err)))))))
ELISP

RUN emacs --batch -l /tmp/precompile-emacspeak.el || true

# Configura early-init.el:
# - JIT desativado: emacspeak está pré-compilado; core do Emacs (AOT Debian) fica intacto.
# - Registra /opt/emacspeak/eln/ no load-path nativo para que os .eln sejam encontrados.
RUN cat > /root/.emacs.d/early-init.el <<'EOF'
(setq native-comp-jit-compilation nil)

(let ((dir "/opt/emacspeak/eln"))
  (dolist (sym '(comp-eln-load-path native-comp-eln-load-path))
    (when (boundp sym)
      (unless (member dir (symbol-value sym))
        (set sym (cons dir (symbol-value sym)))))))
EOF

# Prepara ~/.emacs.d e um diretório específico para o cache .eln.
# A linha startup-redirect-eln-cache força o Emacs a usar esse local,
# o que permite persistência via volume Docker.
RUN mkdir -p /root/.emacs.d "$EMACS_ELN_CACHE" \
 && cat > /root/.emacs.d/init.el <<'EOF_INIT'
;; dtk-program define qual script em emacspeak-servers-directory será lançado.
;; No macOS: DTK_SPEECH_SERVER=espeak-socat (relay TCP via socat)
;; No Linux: deixar vazio (usa espeak padrão via ALSA)
(setq dtk-program
      (let ((s (getenv "DTK_SPEECH_SERVER")))
        (if (and s (not (string= s ""))) s "espeak")))
(setq emacspeak-play-program nil)
(setq emacspeak-use-auditory-icons nil)
(setq emacspeak-line-echo t)
(setq ring-bell-function 'ignore)
;; Mata subprocessos (ex: servidor espeak) automaticamente ao sair do Emacs.
(setq confirm-kill-processes nil)
;; Abre com buffer vazio em vez da tela de boas-vindas.
(setq inhibit-startup-screen t)

;; Redireciona o cache de native compilation para um diretório persistente.
(when (fboundp 'startup-redirect-eln-cache)
  (startup-redirect-eln-cache (or (getenv "EMACS_ELN_CACHE")
                                  (expand-file-name "eln-cache" user-emacs-directory))))

;; variáveis de configuração do Emacspeak.
(defvar emacspeak-xslt-directory "/opt/emacspeak/xsl")
(defvar emacspeak-sounds-dir "/opt/emacspeak/sounds/")

(load-file "/opt/emacspeak/lisp/emacspeak-setup.el")
EOF_INIT

RUN cat > /usr/local/bin/start-emacspeak.sh <<'EOF'
#!/usr/bin/env bash
exec emacs --init-directory=/root/.emacs.d -nw "$@"
EOF
RUN chmod +x /usr/local/bin/start-emacspeak.sh

# Declara o cache como volume persistente.
# Recomenda-se montar um volume nomeado explicitamente em docker run/compose.
VOLUME ["/root/.emacs.d/eln-cache"]

WORKDIR /workspace
CMD ["/usr/local/bin/start-emacspeak.sh"]
