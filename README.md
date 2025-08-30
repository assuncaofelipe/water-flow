# Estória da Tarefa — App com modelo embarcado

**Aplicativo: Água no bolso**

## Visão

> “Vamos criar um aplicativo Android que já carrega dentro do APK os modelos `.tflite` treinados.  
> O usuário importa apenas tabelas já tratadas (colunas limpas e padronizadas).  
> O modelo sabe identificar as colunas correspondentes aos parâmetros, processa e exibe resultados em gráficos e valores.”

## Personagens

- **Técnica/o de campo:** importa a planilha tratada no notebook e vê imediatamente as previsões.
- **Gestora/or ambiental:** abre o app e já vê painéis sem se preocupar com processamento pesado.
- **Cidadã/o curioso:** recebe uma visão simples (água boa/ruim, tendências).

## Narrativa

O técnico coleta dados, roda o pipeline de tratamento no Colab e obtém arquivos “-cleaned.csv”.  
No campo, abre o app Android, escolhe “Importar Tabelas” e seleciona um ou mais CSVs.

O app lê as colunas, consulta o `metadata.json` embarcado (junto do `.tflite`) e encontra automaticamente quais colunas correspondem a DO, pH, Turbidity, Conductivity, E.coli, etc.  
Monta os vetores na ordem certa e envia ao modelo `.tflite` embarcado no APK.

Em segundos, a tela mostra:
- **Cards** com valores previstos (ex.: DO=7.8 mg/L, pH=7.1).
- **Gráficos** de tendência no tempo ou comparando pontos de coleta.
- **Selo** (“OK / Atenção / Crítico”) baseado em limites pré-configurados.

A gestora compara períodos e gera um PDF para compartilhar. Nenhuma rede é necessária: todo o cálculo roda on-device.

---

## Objetivos (DoR → DoD)

### Definition of Ready (DoR)
- Modelos `.tflite` e `metadata.json` estão empacotados em `app/src/main/assets/`.
- Usuário terá apenas CSVs já tratados, com colunas padronizadas.
- Wireframes definidos para cards e gráficos.

### Definition of Done (DoD)
- App carrega modelo `.tflite` do próprio APK.
- CSV importado → parsing → colunas reconhecidas via metadata.
- Vetores formados na ordem de `features_order` → inferência TFLite.
- Resultados exibidos em cards, gráficos e exportáveis em PDF/CSV.
- Funciona offline.

---

## Épicos e Histórias

### Epic 1 — Importação
- **Como usuária, quero importar tabelas já tratadas para o app rodar previsões.**
    - Aceite: seleção de CSVs, preview de colunas, confirmação de importação.

### Epic 2 — Inferência embarcada
- **Como usuária, quero que o app use o modelo `.tflite` interno, sem depender de rede.**
    - Aceite: TFLite carregado de assets; previsão rápida (<200 ms para 100 linhas).
- **Como usuária, quero que o app reconheça automaticamente as colunas corretas.**
    - Aceite: uso do `features_order` do `metadata.json` para mapear colunas.

### Epic 3 — Visualização
- **Como usuária, quero ver cards com indicadores-chave.**
    - Aceite: valores médios/máximos/mínimos, selo de status.
- **Como usuária, quero gráficos claros (linha, barra).**
    - Aceite: gráfico temporal se houver coluna Date.

### Epic 4 — Relatórios
- **Como usuária, quero exportar PDF/CSV com previsões e gráficos.**
    - Aceite: export direto no app, sem rede.

---

## Fluxo de Dados

- **Entrada:** CSV tratado (usuário).
- **Parsing:** Pandas-like lib no Android (ou parsing manual).
- **Mapeamento:** colunas ↔ `features_order` do JSON embarcado.
- **Inferência:** Interpreter TFLite on-device.
- **Exibição:** cards + gráficos + relatório exportável.

---

## Telas

- **Home:** botão “Importar CSV” + lista de arquivos recentes.
- **Prévia:** colunas reconhecidas, n° linhas, alvo selecionado.
- **Resultados:** cards + gráficos, selo de qualidade.
- **Exportar:** PDF/CSV.
- **Configurações:** limites para alertas, seleção de alvos exibidos.

---

## Critérios de Aceite

- App contém `.tflite` e `.json` embutidos (APK).
- Importa CSV tratado e reconhece colunas automaticamente.
- Executa predições offline, exibe valores e gráficos.