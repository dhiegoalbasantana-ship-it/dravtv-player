# DravTV - Aplicativo de IPTV para Android e Android TV

Aplicativo nativo desenvolvido em **Kotlin + Jetpack Compose for TV + Media3 ExoPlayer**, projetado para rodar em smartphones Android, TV Boxes e Smart TVs (Android TV / Google TV).

---

## 🚀 Tecnologias Utilizadas
* **Linguagem:** Kotlin Nativo
* **Interface (UI):** Jetpack Compose + Compose for TV (`androidx.tv:tv-material`)
* **Streaming Player:** AndroidX Media3 (ExoPlayer com suporte a HLS, TS, DASH e MP4)
* **API IPTV:** Xtream Codes API (Autenticação, Canais Ao Vivo, Filmes VOD, Séries e EPG)
* **Rede:** Retrofit 2 + OkHttp 3
* **Imagens:** Coil Compose (carregamento rápido de logotipos e pôsteres)
* **Navegação:** Suporte nativo para **Controle Remoto / D-pad** e **Touchscreen**

---

## 📁 Como Abrir no Android Studio
1. Abra o **Android Studio**.
2. Clique em **File > Open** e selecione a pasta:
   `C:\Users\rafag\.gemini\antigravity\scratch\dravtv`
3. Aguarde o Gradle sincronizar as dependências.
4. Para compilar o APK:
   - Vá no menu superior: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   - O arquivo final estará em: `app/build/outputs/apk/debug/app-debug.apk`.

---

## ☁️ Compilação Automática no GitHub (Grátis)
O projeto já conta com o arquivo de automação `.github/workflows/build-apk.yml`. 
Se você subir este projeto para um repositório no seu GitHub, a nuvem da Microsoft irá compilar o APK gratuitamente a cada alteração.
