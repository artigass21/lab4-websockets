# Lab 4 WebSocket -- Project Report

## Description of Changes

[Detailed description of all changes made]

- Ajustes en `src/test/kotlin/websockets/ElizaServerTest.kt`:
  - Se capturó el tamaño de la lista de mensajes en una variable (`val size = list.size`) tras `latch.await()` para evitar condiciones de carrera al seguir llegando mensajes.
  - Se sustituyó la comprobación rígida por una aserción por **intervalo**: `assertTrue(size in 4..6)` porque la respuesta de Eliza es no determinista.
  - Se verificó el prefijo del flujo de mensajes esperado:  
    `list[0] == "The doctor is in."`, `list[1] == "What's on your mind?"`, `list[2] == "---"`.
  - Se añadió validación de la 4ª respuesta contra un conjunto de **respuestas posibles** (`possibleResponses`) usando `assert(...) { ... }`.
  - Se corrigieron issues de estilo reportados por **ktlint**: eliminación de import no usado, espacios finales, comas de cierre en llamadas multilínea y formato de expresiones multilínea.

## Technical Decisions

[Explanation of technical choices made]

- **Aserción por intervalo en vez de igualdad fija:** la cantidad de mensajes puede variar (respuestas aleatorias), por lo que una igualdad estricta haría frágil el test. Usar un rango mantiene el test estable.
- **Captura del tamaño tras el latch:** aunque `latch.await()` desbloquee, el cliente WebSocket puede seguir recibiendo mensajes; almacenar `list.size` evita leer un valor cambiante durante las aserciones.
- **Listado de respuestas permitidas:** se define `possibleResponses` para validar que la respuesta de Eliza pertenece al dominio esperado sin imponer una secuencia exacta.
- **Cumplimiento de ktlint:** se adoptó el estilo exigido (comas finales en llamadas multilínea y salto de línea para el primer argumento) para garantizar que el CI pase consistentemente.

## Learning Outcomes

[What you learned from this assignment]

- Comprensión de **tests tolerantes a no determinismo** (validaciones por rango y pertenencia a conjuntos).
- Manejo de **sincronización** en pruebas con WebSocket (uso de `CountDownLatch` y captura de valores inestables).
- Prácticas de **limpieza de código** y formateo automático con `ktlint`, y cómo pequeños detalles de estilo pueden romper el CI.
- Mejora en el uso de **JUnit/Kotlin assertions** y criterios de cuándo usar cada una.

## AI Disclosure

### AI Tools Used

- [List specific AI tools used]
- ChatGPT

### AI-Assisted Work

- [Describe what was generated with AI assistance]
- [Percentage of AI-assisted vs. original work]
- [Any modifications made to AI-generated code]
- Correcciones de formato requeridas por `ktlint` (comas finales, multilínea).
- 10% AI-assisted vs 90% original work.

### Original Work

- [Describe work done without AI assistance]
- [Your understanding and learning process]
- Implementación y razonamiento de las aserciones, comentarios explicativos en el test, y resolución manual de los avisos de `ktlint`.
- Comprensión y aplicación de los conceptos de concurrencia con `CountDownLatch` y validación de mensajes en un entorno WebSocket.
