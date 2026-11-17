# 🛡️ KavachPay — UPI & SMS Fraud Shield (Android)

**KavachPay** is an Android cybersecurity and financial fraud-detection application built with **Kotlin** and **Jetpack Compose (Material 3)**. It protects Indian digital payment users against deceptive UPI collect requests, SMS/WhatsApp phishing links, fake bank KYC threats, electricity disconnection scams, part-time job traps, and honeytrap/extortion messages.

---

## 🚀 How the App Works

KavachPay uses a **Hybrid Dual-Layer Fraud Detection Architecture** that combines an **Instant On-Device Heuristic Shield (< 5 ms)** with **Cloud Generative AI Analysis** via **Firebase AI Logic**:

```text
User Input (SMS / WhatsApp / UPI Popup / Sample Pill)
        │
        ▼
┌──────────────────────────────────────────────────────────────┐
│ Layer 1: Instant Local Heuristic Engine (< 5 ms)             │
│ • Evaluates 13 Indian cybercrime & UPI scam pattern rules    │
│ • Detects fake collect requests, suspicious VPAs, shortened  │
│   / APK links, remote-access traps (AnyDesk), honeytraps,    │
│   and verifies genuine bank alerts (A/c XX..., UPI Ref No)   │
│ • Immediately updates UI & saves scan to Room Database       │
└──────────────────────────────────────────────────────────────┘
        │
        ▼  (Runs asynchronously in background for ambiguous custom messages)
┌──────────────────────────────────────────────────────────────┐
│ Layer 2: Firebase AI Logic (Gemini 3.5 Flash-Lite)           │
│ • Protected by Firebase App Check attestation (Zero API Key) │
│ • Enforces strict JSON responseSchema (risk_score,           │
│   threat_level, red_flags, explanation, explanation_hi)      │
│ • Refines assessment & updates Room history non-blockingly   │
└──────────────────────────────────────────────────────────────┘
        │
        ▼
┌──────────────────────────────────────────────────────────────┐
│ Interactive Safety Assessment & Golden-Hour Response         │
│ • 0–100 Risk Score & 3-Zone Progress Bar (Safe / Suspicious  │
│   / High Risk)                                               │
│ • Bilingual Advice (English + हिंदी) with Android TTS Voice  │
│ • Emergency Golden-Hour Checklist (1-Tap 1930 Dialer &       │
│   cybercrime.gov.in reporting when Risk Score >= 35)         │
└──────────────────────────────────────────────────────────────┘
```

### Core Features

1. **Golden UPI Rule 3-Slide Cybercrime Carousel**:
   - Auto-advancing educational banner explaining the 3 Golden Rules of UPI safety (*Collect Requests*, *KYC & APK Phishing*, and *Golden-Hour 1930 Reporting*).
   - Includes manual Previous/Next buttons, swipe gestures, and a direct tap-to-dial **Helpline: 1930** badge.

2. **13-Rule Instant Local Fraud Heuristics (`LocalFraudHeuristics.kt`)**:
   - **Rule 1 — Credential Traps**: Detects requests for `UPI PIN`, `MPIN`, `OTP`, `CVV`, or `Approve & Enter PIN` paired with cashback/reward claims.
   - **Rule 2 — Lottery & Unclaimed Rewards**: Flags fake lucky draws, jackpot claims, and festive cashback lures.
   - **Rule 3 — Deceptive UPI IDs (VPAs)**: Scans handles (`@ybl`, `@okaxis`, etc.) for impersonation keywords (`cashback@`, `refund@`, `kyc@`, `helpline@`).
   - **Rule 4 — Urgency & Disconnection Threats**: Flags panic triggers (`tonight at 9:30 PM`, `blocked today`, `immediately`, `suspended`).
   - **Rule 5 — Authority & Digital Arrest Impersonation**: Detects fake `PAN KYC`, `Electricity Officer`, `Customs Parcel`, `CBI`, `RBI`, and `Digital Arrest` threats.
   - **Rule 6 — Phishing URLs & Shortlinks**: Flags URL shorteners (`bit.ly`, `tinyurl`, `rb.gy`), `.apk` downloads, and suspicious TLDs (`.xyz`, `.top`, `.click`).
   - **Rule 7 — Screen-Sharing & Malware Traps**: Detects remote-control tools (`AnyDesk`, `TeamViewer`, `QuickSupport`, `RustDesk`).
   - **Rule 8 — Unverified Personal Callback Numbers**: Flags personal 10-digit mobile numbers disguised as bank or utility helplines.
   - **Rule 9 — Part-Time Job & Prepaid Task Scams**: Detects YouTube like/subscribe and Telegram VIP investment scams.
   - **Rule 10 — Fake Credit Reversal Scams**: Identifies fake "money sent by mistake, please return" traps.
   - **Rule 11 — Sender Header Mismatch**: Flags bank alerts sent from personal mobile numbers instead of official DLT headers (`VM-HDFCBK`, `AD-SBIBNK`).
   - **Rule 12 — Honeytrap, Friendship Club & Sextortion**: Detects deliberate SMS filter-evasion typos (`Frndsip`, `Patner`, `Feel Alone`) and video-call blackmail threats.
   - **Rule 13 — Verified Safe Bank Alert Guard (`isVerifiedSafeBankAlert`)**: Recognizes authentic bank debit/credit notifications containing masked account numbers (`A/c XX4821`), `UPI Ref No`, `Avl Bal`, and official `1800`/`1860` toll-free numbers so legitimate alerts are never misclassified.

3. **One-Click Sample Scam Pills**:
   - Pre-configured test pills (*Fake UPI Collect*, *Electricity Cut Tonight*, *Honeytrap / Friendship SMS*, *Fake Bank KYC Link*, and *Safe Bank Debit/Credit Alert*) that glow green when active and let users test the engine with one tap.

4. **Bilingual Explanations (`EN + हिंदी`) & Native Voice Read-Out**:
   - Every scan generates context-aware safety advice in both **English** and **Hindi** (including the specific amount, UPI ID, phone number, or link detected in the message).
   - Integrated **Listen (EN)** and **सुनें (हिंदी)** buttons read the safety advice aloud using Android's native `TextToSpeech` engine (`en-IN` and `hi-IN` locales).

5. **Emergency Golden-Hour Checklist (`1930` & `cybercrime.gov.in`)**:
   - Automatically expands and highlights whenever a message scores **Suspicious (`35–69`)** or **High Risk (`70+`)**.
   - Provides a 1-tap **Call 1930 Helpline Now** button, a **Report on cybercrime.gov.in** button, and a 4-step bilingual checklist for the first 15–60 minutes ("Golden Hour") after a fraud incident.

6. **Persistent Local Scan History (Room Database)**:
   - Automatically saves scanned statements, sender headers, risk scores, threat levels, and bilingual explanations locally on the device.
   - Tapping any saved row reloads its full diagnostic report into the Safety Assessment card.

---

## 🤖 AI Models Used

| Layer | Model / Engine | Purpose | Latency |
| :--- | :--- | :--- | :--- |
| **On-Device Primary Shield** | **KavachPay 13-Rule Heuristic Engine** (`LocalFraudHeuristics`) | Instant deterministic pattern matching for Indian UPI, SMS, DLT template, and bank alert structures; extracts amounts, VPAs, links, and phone numbers. | **< 5 ms** (Offline-capable) |
| **Cloud Generative AI** | **Google Gemini 3.5 Flash-Lite** (`gemini-3.5-flash-lite` via **Firebase AI Logic**) | Low-latency contextual LLM classification for novel or ambiguous custom messages using structured JSON output (`Schema.obj`). | **~1–2 s** (Non-blocking background coroutine) |

### Structured JSON Schema Enforced on Gemini (`gemini-3.5-flash-lite`)
Using `generationConfig` in the Firebase AI Logic SDK, the model is constrained to return raw, valid JSON matching this schema:
- `risk_score` (`INTEGER`): Fraud risk score from `0` to `100`
- `threat_level` (`ENUM`): `"Safe"`, `"Suspicious"`, or `"High Risk / Scam"`
- `red_flags` (`ARRAY<STRING>`): Specific manipulation or phishing indicators found
- `explanation` (`STRING`): Actionable safety guidance in English
- `explanation_hi` (`STRING`): Actionable safety guidance in Hindi (`हिंदी`)

---

## 🔌 APIs, SDKs & Android Libraries Used

1. **Firebase AI Logic SDK (`com.google.firebase:firebase-ai`)**:
   - Connects directly to Google's `gemini-3.5-flash-lite` model via `Firebase.ai.generativeModel(...)` without embedding or exposing any client-side `GEMINI_API_KEY` in the APK.

2. **Firebase App Check (`firebase-appcheck-debug` & `firebase-appcheck-recaptcha`)**:
   - Secures all cloud AI requests using attestation tokens (`DebugAppCheckProviderFactory` during development/testing and `RecaptchaAppCheckProviderFactory` in production release builds), initialized in `MainActivity.onCreate()` via `configureAppCheck(this, intent)`.

3. **Android Jetpack Room Persistence Library (`androidx.room:room-runtime`, `room-ktx`, `room-compiler` via KSP)**:
   - Stores scanned statements in a local SQLite database (`kavachpay_scan_history.db`) exposed reactively to the UI via Kotlin `Flow<List<ScanHistoryEntity>>` and `StateFlow`.

4. **Android Native Text-to-Speech API (`android.speech.tts.TextToSpeech`)**:
   - Synthesizes offline/online voice output in **Indian English (`en-IN`)** and **Hindi (`hi-IN`)** with real-time `UtteranceProgressListener` callbacks driving the animated sound-wave UI indicator.

5. **Jetpack Compose & Material Design 3 (`androidx.compose.material3`)**:
   - Full declarative UI with custom **Plus Jakarta Sans** and **Inter** bundled fonts (`res/font/`), adaptive single-column (phone) and two-column (`BoxWithConstraints` tablet/foldable) layouts, and Dark/Light theme switching.

6. **Android Platform System Intents & Services**:
   - `Intent.ACTION_DIAL` (`tel:1930`): Launches the phone dialer with the National Cyber Crime Helpline pre-filled.
   - `Intent.ACTION_VIEW` (`https://cybercrime.gov.in`): Opens the official National Cyber Crime Reporting Portal.
   - `ClipboardManager`: Copies the formatted bilingual safety report to the system clipboard via the **Share Advice** button.

---

## 📂 Project Structure

```text
app/src/main/
├── AndroidManifest.xml                  # Permissions (INTERNET) & TTS service queries
├── java/com/example/
│   ├── MainActivity.kt                  # App Check initialization, Edge-to-Edge & Compose host
│   ├── data/
│   │   ├── KavachDatabase.kt            # Room SQLite Database singleton
│   │   ├── ScanHistoryDao.kt            # Reactive Flow queries & suspend insert/clear operations
│   │   ├── ScanHistoryEntity.kt         # Entity model & JSON flag serializer
│   │   └── ScanRepository.kt            # Data repository with consecutive-scan deduplication
│   ├── domain/
│   │   └── LocalFraudHeuristics.kt      # 13-rule Indian UPI & SMS fraud detection engine
│   ├── model/
│   │   └── FraudModels.kt               # Domain data classes, sample scenarios & Golden Rules
│   ├── ui/
│   │   ├── KavachPayScreen.kt           # Adaptive mobile & tablet/foldable screen scaffold
│   │   ├── KavachViewModel.kt           # MVVM state holder, Gemini AI Logic & TTS controller
│   │   ├── components/
│   │   │   ├── HeaderAndCarouselComponents.kt # Top bar & 3-slide Golden UPI Rule carousel
│   │   │   ├── InputAndHistoryComponents.kt   # Scanner input card, sample pills & history table
│   │   │   └── SafetyAssessmentComponents.kt  # Threat meter, bilingual advice & 1930 checklist
│   │   └── theme/
│   │       ├── Color.kt                 # Teal, Emerald, Amber, Rose, Slate & Zinc palettes
│   │       ├── Theme.kt                 # Light & Dark Material 3 color schemes
│   │       └── Type.kt                  # Bundled Plus Jakarta Sans & Inter typography
│   └── util/
│       └── AppCheckHelper.kt            # Synchronous debug token & release reCAPTCHA setup
└── res/
    ├── drawable/                        # Custom shield-check adaptive launcher icon vectors
    ├── font/                            # Local static .ttf fonts (plus_jakarta_sans, inter)
    └── values/                          # strings.xml, themes.xml & firebase_applet_config.xml
```

---

## 🛠️ How to Run & Build APK / AAB

### 1. Running in AI Studio / Android Studio
- **In Google AI Studio**: The app compiles automatically and runs in the built-in **Streaming Android Emulator**.
- **In Android Studio**:
  1. Clone or export the project ZIP and open it in **Android Studio Ladybug (or newer)** with **JDK 17+**.
  2. Let Gradle sync dependencies (`app/google-services.json` is already included).
  3. Select an Android device or emulator (API 24+) and click **Run ▶**.

### 2. Building the `.apk` (for Direct Mobile Testing) and `.aab` (for Google Play Console)
Pre-built packages are already generated and available at:
- **APK (Direct Mobile Install)**: `/src/KavachPay.apk` and `/app/src/KavachPay.apk`
- **AAB (Google Play Console Upload)**: `/src/KavachPay.aab` and `/app/src/KavachPay.aab`

To rebuild them from the terminal using Gradle:

```bash
# Build Debug APK and Android App Bundle (.aab)
gradle :app:assembleDebug :app:bundleDebug

# Run Unit & Robolectric Tests
gradle :app:testDebugUnitTest
```

The standard Gradle outputs are written to:
- **APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **AAB**: `app/build/outputs/bundle/debug/app-debug.aab`

- DEMO VIDEO LINK --> https://youtu.be/l3_W9Xai580?si=dvmYJ1Cq4IGysOfx
