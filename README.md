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


------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




🚗 Bomba & Ficha - Intelligent Vehicle Manager
Note: Final project developed for the Mobile Device Programming course.

📋 About the Project
Bomba & Ficha is a native Android application (Java) designed to solve a modern problem: the simultaneous management of Combustion and Electric vehicles in the same garage.

Unlike traditional fuel calculators, this app offers robust data persistence, detailed statistics, and specific tools for electric car drivers (such as range calculation and kWh consumption).

✨ Key Features
🚙 Fleet Management (My Garage)
Multi-Vehicle: Support for adding infinite vehicles.

Hybrid: Native support for Combustion (Gasoline/Diesel) and Electric (EV) vehicles.

Design: Clean interface with "Empty States" (illustrations when the list is empty).

⛽ Refueling & Charging Management
Complete History: Log of Kms, Total Cost, and Quantity.

Adaptive Units: The app automatically switches between Liters (L) and Kilowatts (kWh) depending on the selected car.

CRUD: Ability to Add, Edit, and Delete individual records (via long click).

📊 Statistics & Charts
Dashboard: Automatic calculation of:

Total Spent (€).

Average Consumption (L/100km or kWh/100km).

Visualization: Bar charts (MPAndroidChart) to visualize the evolution of monthly expenses.

⚡ Exclusive EV Features (Electric Mode)
Range Calculator: Algorithm that estimates range based on current battery % and the driver's historical consumption average.

Trip Estimation: Cost forecast for a specific trip.

💰 Freemium Model
AdMob: Integration of banner ads for free users.

Google Play Billing: Code structure ready for in-app purchases (Pro Version) that remove ads and unlock advanced features.

🛠️ Architecture and Technologies
This project follows modern Android development best practices:

Language: Java 17

Data Persistence (Local):

Room Database (SQLite): For structured data (Vehicles, Refueling logs). Uses One-to-Many relations with ForeignKeys and CASCADE delete.

SharedPreferences: For simple data (User name, Pro State, Mode preferences).

User Interface (UI):

XML Layouts.

Material Design Components (TextInputLayout, MaterialCardView, FloatingActionButton).

RecyclerView with custom adapters and ViewBinding.

Performance:

Threading: All database operations are executed in Background Threads (ExecutorService) to ensure the UI never blocks (ANR).

External Libraries:

MPAndroidChart (Charts).

Google Play Services Ads (AdMob).

Google Play Billing Client (Payments).

🔧 How to Run
Clone the Repository:

Bash

git clone https://github.com/your-username/bomba-e-ficha.git
Open in Android Studio:

Make sure you have JDK 17 configured in your Gradle settings.

Sync: Let Gradle download all dependencies.

Run: Launch the app on an emulator (recommended: Pixel, API 34+) or physical device.

📝 Database Structure
The combustivel_database consists of two main entities:

Veiculo Table (Vehicle) | Column | Type | Description | | :--- | :--- | :--- | | id | INT (PK) | Unique identifier | | nome | TEXT | Car name | | tipoVeiculo | TEXT | "COMBUSTAO" (Combustion) or "ELETRICO" (Electric) | | capacidadeBateria | REAL | Only for EVs (kWh) |

Abastecimento Table (Refueling/Charging) | Column | Type | Description | | :--- | :--- | :--- | | id | INT (PK) | Unique identifier | | veiculoId | INT (FK) | Foreign key to Veiculo | | litros | REAL | Quantity (L or kWh) | | custoTotal | REAL | Price paid (€) |

👤 Author
Tiago Pereira

Academic Project UBI

📄 License
This project is for educational purposes.

