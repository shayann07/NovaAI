# NovaAI

NovaAI is a modern Android chat application that lets you talk to a powerful AI assistant, similar to ChatGPT.  Built with a clean, modular architecture, it integrates the **Together API** and **OpenRouter** so you can ask questions, brainstorm ideas, or simply have fun conversations with the assistant.  Smooth typing animations and a responsive interface make interactions feel natural and engaging.

## Features

- **Conversational AI chat** – interact with an AI assistant using natural language.  Ask questions, get explanations, and generate ideas in real time.
- **Powered by Together API & OpenRouter** – the app sends your prompts to Together API/OpenRouter behind the scenes and returns high‑quality responses.
- **Typing animation** – displays an animated typing indicator so conversations feel more lifelike.
- **Clean MVVM architecture** – separates the presentation, domain and data layers for testability and scalability.
- **Modern Android design** – built with Material‑design guidelines for a clean, intuitive interface.
- **Open source and extensible** – clone the project, add your own API keys and customise the UI or logic to fit your needs.

## Tech stack

| Layer            | Technologies                            |
|------------------|-----------------------------------------|
| **Language**     | Kotlin                                  |
| **Architecture** | Model–View–ViewModel (MVVM)             |
| **UI**           | Android Views / Material Components     |
| **Networking**   | Retrofit for HTTP, OkHttp               |
| **Dependency Injection** | Hilt (Dagger)                   |
| **Asynchronous** | Kotlin Coroutines, Flow                 |
| **APIs**         | Together API, OpenRouter                |

> *Note:* The technologies listed above are inferred from typical Android chat‑app implementations; consult the `build.gradle.kts` files for exact dependencies.

## Getting started

1. **Clone the repository**

   ```bash
   git clone https://github.com/shayann07/NovaAI.git
   cd NovaAI
   ```

2. **Obtain API keys**

   - Sign up for the [Together API](https://platform.together.xyz) and [OpenRouter](https://openrouter.ai) and create API keys.
   - In the project, create a file called `local.properties` (if it doesn’t already exist) and add entries like:

     ```properties
     TOGETHER_API_KEY=your_together_api_key
     OPENROUTER_API_KEY=your_openrouter_api_key
     ```

3. **Open in Android Studio**

   - Open the project using the latest version of **Android Studio**.
   - Let Gradle sync and download dependencies.
   - Connect an emulator or Android device, then click **Run** to build and deploy the app.

## Contributing

Pull requests are welcome!  If you’d like to add new features, improve the UI, or integrate other AI providers, feel free to fork the repo and submit your changes.

## License

This project is licensed under the [MIT License](LICENSE).
