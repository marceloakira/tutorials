# Tutorial: Instalação acessível para programação em Java (Emacs + Emacspeak)

## 🎯 Objetivo
Configurar um ambiente Java acessível com:
- Emacs + Emacspeak (leitura por voz)
- Execução simplificada com F5
- Suporte a Scanner (entrada interativa)
- Autocomplete (company)
- Inteligência Java (lsp-java)

---

## 🧱 1. Pré-requisitos

### 🍎 macOS
```bash
brew install openjdk
brew install emacs
```

Adicione ao PATH:
```bash
echo 'export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

---

### 🐧 Ubuntu
```bash
sudo apt update
sudo apt install openjdk-17-jdk emacs espeak
```

---

### 🔍 Verificação
```bash
java -version
javac -version
emacs --version
```

---

## 🔊 2. Instalar Emacspeak (macOS e Ubuntu)

```bash
git clone https://github.com/tvraman/emacspeak.git
cd emacspeak
make config
make
```

Teste:
```bash
cd servers
./espeak
```

---

## ⚙️ 3. Configuração do Emacs

Baixe o arquivo [init.el](./init.el) disponível na mesma pasta deste tutorial e coloque no diretório de configuração do Emacs (geralmente `~/.emacs.d/`), por exemplo:

```bash
mv init.el ~/.emacs.d/
```

No `init.el`, ajuste o caminho do Emacspeak:

### macOS
```elisp
(load-file "/Users/SEU_USUARIO/emacspeak/lisp/emacspeak-setup.el")
```

### Ubuntu
```elisp
(load-file "/home/SEU_USUARIO/emacspeak/lisp/emacspeak-setup.el")
```

---

## 📦 4. Instalar pacotes no Emacs

Abra o Emacs:

```
M-x package-refresh-contents
M-x package-install RET company RET
M-x package-install RET lsp-mode RET
M-x package-install RET lsp-java RET
```

---

## 🔊 5. Testar acessibilidade

Ao abrir o Emacs:
- Deve haver leitura automática

Testes:
```
C-e  → lê linha
C-n  → próxima linha
```

---

## 💡 6. Execução com F5

Pressione:
```
F5
```

O sistema:
1. Compila
2. Executa se não houver erro
3. Abre buffer interativo
4. Vocaliza saída

---

## 🧪 7. Exemplo básico

```java
public class Ola {
    public static void main(String[] args) {
        System.out.println("Olá mundo");
    }
}
```

Pressione F5.

---

## 📦 8. Exemplo com package

```java
package tests;

public class Ola {
    public static void main(String[] args) {
        System.out.println("Olá com package");
    }
}
```

Funciona mesmo fora da pasta correta.

---

## ⌨️ 9. Entrada com Scanner

```java
import java.util.Scanner;

public class Entrada {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Digite seu nome:");
        String nome = sc.nextLine();
        System.out.println("Olá " + nome);
    }
}
```

Uso:
1. F5
2. Digite no buffer
3. ENTER

---

## 🧠 10. Autocomplete

```
M-/  → sugestões
C-n  → próximo
C-p  → anterior
ENTER → confirma
```

---

## 🔍 11. LSP

```
M-x lsp-describe-session
```

---

## ⚠️ 12. Problemas comuns

### Emacspeak não fala
- Verifique caminho do emacspeak no init.el
- Verifique espeak instalado

### F5 não funciona
- Arquivo salvo
- Classe com main

---

## 📂 13. Estrutura de build

O sistema cria automaticamente:
```
.emacs-java-build/
```

---

## ♿ 14. Boas práticas

- Use ambiente textual
- Evite IDEs pesadas
- Prefira atalhos simples (F5)
- Use leitura contínua

---

## 🚀 Conclusão

Ambiente leve, acessível e ideal para ensino de Java e POO.
