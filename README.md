# ESG App (Android Kotlin)

MVP simples para faculdade com foco em **ESG - pilar Ambiental**.

## Ideia do projeto
Aplicativo nativo Android que ajuda o usuário a:
- simular impacto ambiental diário de deslocamento (CO₂ estimado),
- consultar indicador real de qualidade do ar (PM2.5) via serviço externo,
- registrar uma meta sustentável semanal.

## Serviço externo consumido
- **Open-Meteo Air Quality API** (HTTPS, sem necessidade de backend próprio):
  - Base URL: `https://air-quality-api.open-meteo.com/`
  - Endpoint usado: `/v1/air-quality?latitude={lat}&longitude={lon}&current=pm2_5`

## Telas (mínimo 5)
1. Login simples
2. Início (menu)
3. Simulador de impacto ambiental
4. Qualidade do ar (API)
5. Dicas e metas

## Como abrir
1. Abra a pasta no Android Studio.
2. Aguarde sincronização do Gradle.
3. Rode em emulador ou celular Android.

## Geração para entrega
- Gere APK em **Release** (`Build > Generate Signed Bundle / APK`).
- Não coloque o APK dentro da pasta do projeto.
- Compacte um ZIP contendo:
  - pasta do projeto,
  - APK release separado,
  - PDF do relatório.
