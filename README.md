# NovaAI

A small Kotlin Android chat client that POSTs the user's prompt to the **Together.xyz** OpenAI-compatible Chat Completions endpoint and renders the assistant's reply in a Material RecyclerView. The model is hardcoded to `mistralai/Mistral-7B-Instruct-v0.2`. Single screen, single API call, Retrofit + OkHttp + a small ViewModel.

> **Heads-up:** the previous README listed Hilt, Flow, OpenRouter, and an MIT license, with an explicit caveat that the stack was "inferred from typical Android chat-app implementations". It does not match the code. The real stack is Retrofit + OkHttp + Coroutines + LiveData; only **Together.xyz** is integrated; there is no Hilt, no Flow, and no `LICENSE` file. See [Honest limitations](#honest-limitations).

## ⚠ Security Notice — Read first

`app/src/main/java/com/nayab/aichat/api/RetrofitClient.kt` ships a **live Together.xyz API key as a Kotlin `private const val`**:

```kotlin
private const val OPENROUTER_API_KEY = "eff45663…d63961"   // line 15
```

The constant is misnamed — the base URL is `https://api.together.xyz/`, so it is a Together key. The key is committed to git history (initial commit `a8836f3`) and the repo is public. Treat it as compromised:

1. **Rotate at Together.xyz immediately.**
2. **Remove from git history** (`git filter-repo` / equivalent) and force-push, or rewrite history before publishing further.
3. Move the new key to `local.properties` + a `buildConfigField` and never source-commit it.

`RetrofitClient` also installs `HttpLoggingInterceptor.Level.BODY` unconditionally (line 19), so every request and response — including the `Authorization: Bearer …` header — is dumped to Logcat. Gate this on `BuildConfig.DEBUG` before shipping.

## Status

- Working tree clean on `master`, two commits in history (`a8836f3` "Initial Commit", `ad530d0` "Add README with project overview and setup").
- Remote: `https://github.com/shayann07/NovaAI.git`.
- This README was rewritten from a code audit; the previous one's stack list was self-described as inferred and does not match the source.
- The `HTTP-Referer` header in `RetrofitClient` points to `https://github.com/Rarenayab520/Ai-Chat`, suggesting this is a fork/copy of that upstream project.

## How it works

- **`ui/MainActivity`** is the only activity. Inflates `activity_main.xml` (dark `#1E1E1E` Material toolbar, RecyclerView, rounded `CardView` input row), instantiates `ChatViewModel` directly (no DI, no factory), wires a `ChatAdapter`, and observes `messages: LiveData<List<Message>>`. A `TextWatcher` on the input enables/disables the send button.
- **`viewmodel/ChatViewModel`** holds a `MutableLiveData<List<Message>>`. On send it appends the user message + a placeholder bot message with `isTyping = true`, starts a `Handler`-based dot animation that mutates the placeholder text between `.`, `..`, `...` every 500 ms, then `viewModelScope.launch`-es a call to `ChatRepository.getReply(messages)`. On reply it cancels the animation and replaces the placeholder.
- **`data/ChatRepository.getReply(messages)`** calls `RetrofitClient.api.getChatCompletion(...)` with `model = "mistralai/Mistral-7B-Instruct-v0.2"` (hardcoded). No streaming.
- **`api/ApiService`** is a single Retrofit interface: `@POST("v1/chat/completions") suspend fun getChatCompletion(@Body request: ChatRequest): ChatResponse`.
- **`api/RetrofitClient`** is a Kotlin `object` that builds the Retrofit + OkHttp stack with a body-level `HttpLoggingInterceptor` and a header interceptor adding `Authorization`, `HTTP-Referer`, and `Content-Type`.
- **`model/Message`** has `role`, `content`, `isTyping`. **`ChatRequest`** has `model` + `messages`. **`ChatResponse`** parses `choices[].message`.
- **`ui/adapter/ChatAdapter`** has two view types: `item_message_user.xml` (right-aligned text) and `item_message_bot.xml` (text + a Lottie `loading.json` animation visible while `isTyping == true`).

## Tech stack

- **Language / build:** Kotlin, AGP via `libs.versions.toml`, View Binding enabled.
- **App config:** `applicationId = com.nayab.aichat`.
- **Dependencies:** Retrofit 2.9.0 + Gson converter, OkHttp 4.12.0 + `logging-interceptor`, `kotlinx-coroutines-android` 1.7.3, AndroidX `lifecycle-viewmodel-ktx` + `livedata-ktx`, `material`, RecyclerView, Lottie 6.0.1, plus the default JUnit / AndroidX-JUnit / Espresso scaffolds.
- **Permissions:** `INTERNET` only.

The repo does **not** use Hilt / Dagger, Flow, Compose, Navigation Component, OpenRouter (any SDK), Together's official SDK, Room, DataStore, or WorkManager.

## Project layout

```
NovaAI/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/nayab/aichat/
│       │   ├── api/         ApiService.kt, RetrofitClient.kt   # ⚠ hardcoded key + BODY logging
│       │   ├── data/        ChatRepository.kt                  # hardcoded model id
│       │   ├── model/       Message.kt, ChatRequest.kt, ChatResponse.kt
│       │   ├── ui/          MainActivity.kt, adapter/ChatAdapter.kt
│       │   └── viewmodel/   ChatViewModel.kt
│       └── res/  layout/{activity_main, item_message_user, item_message_bot}.xml,
│                 raw/loading.json (Lottie)
├── local.properties              # ⚠ committed (see Follow-ups)
├── build/                        # ⚠ committed (no root .gitignore)
└── .gradle/, .idea/              # ⚠ committed
```

## Setup / run

1. **Rotate and replace the API key first** (see [Security Notice](#-security-notice--read-first)). Once rotated, the in-source key is dead and the app will get 401s until you wire in a new one.
2. Clone, open in Android Studio, let Gradle sync, run.

## Honest limitations

- **One provider, not two.** Previous README listed Together API + OpenRouter; only Together.xyz is wired up.
- **No Hilt, no Flow.** `ChatViewModel` is created directly, dependencies are constructed in objects, and async output is `LiveData`, not `Flow`.
- **Hardcoded model id.** `mistralai/Mistral-7B-Instruct-v0.2` is a constant in `ChatRepository`; there is no model picker or settings screen.
- **No `LICENSE` file** at the repo root, despite the previous README linking to one.
- **`local.properties` is committed.** Add a root `.gitignore` (and untrack `build/`, `.gradle/`, `.idea/` while you're at it).
- **No `.gitignore` at the repo root.** `build/`, `.gradle/`, `.idea/` are all tracked.
- **HTTP body logging is always on.** `HttpLoggingInterceptor.Level.BODY` is set unconditionally; gate it on `BuildConfig.DEBUG`.
- **`HTTP-Referer` header** points to `https://github.com/Rarenayab520/Ai-Chat`; this repo is likely a fork/copy of that upstream and should attribute it.
- **`OPENROUTER_API_KEY` constant name** is misleading — it's a Together key. Rename after rotation.
- **No tests** beyond the default `ExampleUnitTest` / `ExampleInstrumentedTest`.
