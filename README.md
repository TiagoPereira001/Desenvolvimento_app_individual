# 🚗 Bomba & Ficha - Gestor de Veículos Inteligente

> **Nota:** Projeto final desenvolvido no âmbito da disciplina de Programação de Dispositivos Móveis.


## 📋 Sobre o Projeto

**Bomba & Ficha** é uma aplicação Android nativa (Java) desenhada para resolver um problema moderno: a gestão simultânea de veículos a **Combustão** e **Elétricos** na mesma garagem.

Ao contrário das calculadoras de combustível tradicionais, esta app oferece uma persistência de dados robusta, estatísticas detalhadas e ferramentas específicas para condutores de carros elétricos (como cálculo de autonomia e consumo em kWh).

---

## ✨ Funcionalidades Principais

### 🚙 Gestão de Frota (A Minha Garagem)
* **Multi-Veículo:** Suporte para adicionar infinitos veículos.
* **Híbrido:** Suporte nativo para veículos de **Combustão** (Gasolina/Gasóleo) e **Elétricos** (EV).
* **Design:** Interface limpa com "Empty States" (ilustrações quando a lista está vazia).

### ⛽ Gestão de Abastecimentos
* **Histórico Completo:** Registo de Kms, Custo Total e Quantidade.
* **Unidades Adaptáveis:** A app alterna automaticamente entre **Litros (L)** e **Quilowatts (kWh)** dependendo do carro selecionado.
* **CRUD:** Possibilidade de Adicionar, Editar e Apagar registos individuais (com clique longo).

### 📊 Estatísticas e Gráficos
* **Dashboard:** Cálculo automático de:
    * Total Gasto (€).
    * Média de Consumo (L/100km ou kWh/100km).
* **Visualização:** Gráficos de barras (**MPAndroidChart**) para visualizar a evolução dos gastos mensais.

### ⚡ Funcionalidades Exclusivas EV (Modo Elétrico)
* **Range Calculator:** Algoritmo que calcula a autonomia estimada com base na % de bateria atual e na média histórica de consumo do condutor.
* **Estimativa de Viagem:** Previsão de custos para uma viagem específica.

### 💰 Modelo Freemium
* **AdMob:** Integração de banners publicitários para utilizadores gratuitos.
* **Google Play Billing:** Estrutura de código pronta para compras in-app (Versão Pro) que remove anúncios e desbloqueia funcionalidades avançadas.

---

## 🛠️ Arquitetura e Tecnologias

Este projeto segue as melhores práticas de desenvolvimento Android moderno:

* **Linguagem:** Java 17
* **Persistência de Dados (Local):**
    * **Room Database (SQLite):** Para dados estruturados (Veículos, Abastecimentos). Utiliza relações *One-to-Many* com `ForeignKeys` e `CASCADE` delete.
    * **SharedPreferences:** Para dados simples (Nome do utilizador, Estado Pro, Preferências de Modo).
* **Interface (UI):**
    * XML Layouts.
    * **Material Design Components** (`TextInputLayout`, `MaterialCardView`, `FloatingActionButton`).
    * **RecyclerView** com adaptadores personalizados e *ViewBinding*.
* **Performance:**
    * **Threading:** Todas as operações de base de dados são executadas em *Background Threads* (`ExecutorService`) para garantir que a UI nunca bloqueia (ANR).
* **Bibliotecas Externas:**
    * `MPAndroidChart` (Gráficos).
    * `Google Play Services Ads` (AdMob).
    * `Google Play Billing Client` (Pagamentos).

---

## 🔧 Como Executar

1.  **Clonar o Repositório:**
    ```bash
    git clone [https://github.com/teu-username/bomba-e-ficha.git](https://github.com/teu-username/bomba-e-ficha.git)
    ```
2.  **Abrir no Android Studio:**
    * Certifica-te que tens o **JDK 17** configurado nas definições do Gradle.
3.  **Sincronizar:** Deixa o Gradle descarregar todas as dependências.
4.  **Executar:** Corre a app num emulador (recomendado: Pixel, API 34+) ou dispositivo físico.

---

## 📝 Estrutura da Base de Dados

A base de dados `combustivel_database` é composta por duas entidades principais:

**Tabela `Veiculo`**
| Coluna | Tipo | Descrição |
| :--- | :--- | :--- |
| `id` | INT (PK) | Identificador único |
| `nome` | TEXT | Nome do carro |
| `tipoVeiculo` | TEXT | "COMBUSTAO" ou "ELETRICO" |
| `capacidadeBateria` | REAL | Apenas para EVs (kWh) |

**Tabela `Abastecimento`**
| Coluna | Tipo | Descrição |
| :--- | :--- | :--- |
| `id` | INT (PK) | Identificador único |
| `veiculoId` | INT (FK) | Chave estrangeira para `Veiculo` |
| `litros` | REAL | Quantidade (L ou kWh) |
| `custoTotal` | REAL | Preço pago (€) |

---

## 👤 Autor

**Tiago Pereira**
* Projeto Académico UBI

---

## 📄 Licença

Este projeto é para fins educativos.

