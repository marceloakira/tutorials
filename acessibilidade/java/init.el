;;; init-java-f5-unificado.el --- Emacs acessível para Java com Emacspeak -*- lexical-binding: t; -*-

;;; Pacotes
(require 'package)
(add-to-list 'package-archives '("melpa" . "https://melpa.org/packages/") t)
(package-initialize)

;;; Emacspeak
(setenv "DTK_PROGRAM" "espeak")
(setenv "ESPEAK_VOICE" "pt-br")

(setq emacspeak-play-program nil)
(setq emacspeak-use-auditory-icons nil)
(setq emacspeak-line-echo t)
(setq echo-keystrokes 0.1)
(setq ring-bell-function 'ignore)

(load-file "/Users/akira/dados/dev/emacspeak/lisp/emacspeak-setup.el")

;;; Configurações gerais
(savehist-mode 1)
(fido-mode 1)
(global-hl-line-mode 1)
(column-number-mode 1)
(line-number-mode 1)
(setq line-move-visual nil)

(with-eval-after-load 'info
  (define-key Info-mode-map (kbd ",") #'Info-search-next))

;;; Company
(require 'company)
(setq company-idle-delay nil)
(setq company-minimum-prefix-length 1)
(setq company-tooltip-align-annotations t)
(setq company-show-quick-access nil)

(add-hook 'after-init-hook #'global-company-mode)
(global-set-key (kbd "M-/") #'company-complete)

(with-eval-after-load 'company
  (define-key company-active-map (kbd "C-n") #'company-select-next)
  (define-key company-active-map (kbd "C-p") #'company-select-previous)
  (define-key company-active-map (kbd "<tab>") #'company-complete-selection)
  (define-key company-active-map (kbd "TAB") #'company-complete-selection)
  (define-key company-active-map (kbd "RET") #'company-complete-selection))

;;; LSP Java
(require 'lsp-mode)
(require 'lsp-java)

(setq lsp-prefer-flymake nil)
(setq lsp-eldoc-enable-hover t)

(with-eval-after-load 'lsp-mode
  (add-to-list 'lsp-disabled-clients 'semgrep-ls))

(with-eval-after-load 'lsp-ui
  (setq lsp-ui-doc-enable nil)
  (setq lsp-ui-sideline-enable nil))

(add-hook 'java-mode-hook #'lsp-deferred)

;;; Feedback ao salvar
(defun my-speak-saved ()
  "Anuncia que o arquivo foi salvo."
  (message "Arquivo salvo.")
  (when (fboundp 'emacspeak-speak-line)
    (emacspeak-speak-line)))

(add-hook 'after-save-hook #'my-speak-saved)

;;; Java unificado em F5
(defvar-local my-java-run-command nil
  "Comando java a ser executado após compilação bem-sucedida.")

(defun my-java--buffer-package-name ()
  "Retorna o package declarado no buffer atual, ou nil."
  (save-excursion
    (goto-char (point-min))
    (when (re-search-forward
           "^[[:space:]]*package[[:space:]]+\\([A-Za-z0-9_.]+\\)[[:space:]]*;"
           nil t)
      (match-string-no-properties 1))))

(defun my-java--main-class-name (file package)
  "Retorna o nome qualificado da classe principal."
  (let ((class (file-name-base file)))
    (if (and package (not (string-empty-p package)))
        (concat package "." class)
      class)))

(defun my-java--build-dir (file)
  "Retorna o diretório de build usado para compilar classes Java."
  (expand-file-name ".emacs-java-build"
                    (file-name-directory (expand-file-name file))))

(defun my-java--interactive-buffer-name ()
  "Nome do buffer interativo de execução."
  "*Java Run*")

(defun my-java--speak-buffer-if-available (buffer)
  "Vocaliza BUFFER quando possível."
  (when (buffer-live-p buffer)
    (with-current-buffer buffer
      (goto-char (point-min))
      (when (fboundp 'emacspeak-speak-buffer)
        (emacspeak-speak-buffer)))))

(defun my-java--prepare-run-buffer ()
  "Cria e prepara um buffer limpo para entrada e saída do programa."
  (let ((buf (get-buffer-create (my-java--interactive-buffer-name))))
    (with-current-buffer buf
      (read-only-mode -1)
      (erase-buffer)
      (shell-mode)
      (goto-char (point-max)))
    buf))

(defun my-java--start-interactive-run (cmd)
  "Executa CMD em um shell interativo limpo, sem exibir o comando."
  (let ((buf (my-java--prepare-run-buffer)))
    (pop-to-buffer buf)
    (goto-char (point-max))
    (let ((proc (get-buffer-process buf)))
      (unless (and proc (process-live-p proc))
        (shell buf)
        (setq proc (get-buffer-process buf)))
      (goto-char (point-max))
      (comint-clear-buffer)
      (goto-char (point-max))
      (process-send-string proc (concat cmd "\n"))
      (message "Programa Java em execução.")
      (when (fboundp 'emacspeak-auditory-icon)
        (emacspeak-auditory-icon 'select-object)))))

(defun my-java--compilation-finish (buffer status)
  "Se compilou com sucesso, executa a classe associada em buffer interativo limpo."
  (when (buffer-live-p buffer)
    (with-current-buffer buffer
      (if (and my-java-run-command
               (string-match "finished" status))
          (my-java--start-interactive-run my-java-run-command)
        (progn
          (pop-to-buffer buffer)
          (goto-char (point-min))
          (when (fboundp 'emacspeak-auditory-icon)
            (emacspeak-auditory-icon 'warn-user))
          (my-java--speak-buffer-if-available buffer))))))

(add-hook 'compilation-finish-functions #'my-java--compilation-finish)

(defun run-java ()
  "Compila e executa a classe Java atual usando um único fluxo interativo."
  (interactive)
  (if-let ((file (buffer-file-name)))
      (let* ((package (my-java--buffer-package-name))
             (main-class (my-java--main-class-name file package))
             (build-dir (my-java--build-dir file))
             (compile-cmd (format "mkdir -p %s && javac -d %s %s"
                                  (shell-quote-argument build-dir)
                                  (shell-quote-argument build-dir)
                                  (shell-quote-argument file)))
             (run-cmd (format "java -cp %s %s"
                              (shell-quote-argument build-dir)
                              main-class))
             (buf (compilation-start
                   compile-cmd
                   'compilation-mode
                   (lambda (_) "*Java Compile*"))))
        (with-current-buffer buf
          (setq-local my-java-run-command run-cmd))
        (message "Compilando %s..." main-class))
    (message "Este buffer não está associado a um arquivo.")))

(global-set-key (kbd "<f5>") #'run-java)

(custom-set-variables
 '(package-selected-packages
   '(company lsp-java lsp-mode lsp-ui)))

(custom-set-faces)
