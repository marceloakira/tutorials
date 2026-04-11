# 📘 Tutorial: Organizando o `init.el` em Módulos no Emacs

Este tutorial mostra como organizar seu `init.el` de forma simples, modular e fácil de manter.

A ideia é transformar o `init.el` em um arquivo **enxuto**, que apenas carrega módulos separados por responsabilidade.

---

## 🧩 Estrutura final recomendada

```text
~/.emacs.d/
├── init.el
├── custom.el
└── lisp/
    ├── init-packages.el
    ├── init-accessibility.el
    ├── init-core.el
    ├── init-navigation.el
    ├── init-completion.el
    ├── init-java.el
    ├── init-gptel.el
    ├── init-shell.el
    ├── init-layout.el
    └── init-activities.el
```

---

## 📄 1. Arquivo principal: `init.el`

Seu `init.el` deve ser pequeno e direto:

```elisp
;;; init.el --- Configuração modular do Emacs -*- lexical-binding: t; -*-

;; Diretório de módulos
(add-to-list 'load-path (expand-file-name "lisp" user-emacs-directory))

;; Mantém customizações automáticas separadas do código manual.
(setq custom-file (expand-file-name "custom.el" user-emacs-directory))
(load custom-file t)

(require 'init-packages)
(require 'init-accessibility)
(require 'init-core)
(require 'init-navigation)
(require 'init-completion)
(require 'init-java)
(require 'init-gptel)
(require 'init-shell)
(require 'init-layout)
(require 'init-activities)

;;; init.el ends here
```

---

## 🎯 2. Princípio da modularização

Cada arquivo em `lisp/` deve ter **uma única responsabilidade**.

| Módulo | Responsabilidade |
|------|----------------|
| `init-packages.el` | Gerenciamento de pacotes |
| `init-accessibility.el` | Emacspeak e acessibilidade |
| `init-core.el` | Configurações básicas |
| `init-navigation.el` | Navegação |
| `init-completion.el` | Autocomplete |
| `init-java.el` | Java |
| `init-gptel.el` | IA |
| `init-shell.el` | Terminal |
| `init-layout.el` | Layout |
| `init-activities.el` | Fluxos |

---

## ⚙️ 3. Exemplo de módulo

```elisp
;;; init-core.el -*- lexical-binding: t; -*-

(setq inhibit-startup-screen t)
(menu-bar-mode -1)

(provide 'init-core)
```

---

## 📁 4. Onde fica o `custom.el`

```elisp
(setq custom-file (expand-file-name "custom.el" user-emacs-directory))
(load custom-file t)
```

👉 Fica em: `~/.emacs.d/custom.el`

---

## 🚀 5. Criando módulos

1. `C-x d`
2. entrar em `~/.emacs.d/lisp`
3. `C-x C-f init-core.el`
4. salvar

---

## 🧠 6. Boas práticas

- Um módulo = uma responsabilidade
- Use nomes claros
- Sempre `(provide 'modulo)`
- Evite arquivos genéricos

---

## 🎯 Conclusão

- init.el limpo
- código organizado
- fácil manutenção
- ideal para Emacspeak
