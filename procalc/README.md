# ProCalc

Spring Boot + Thymeleaf scientific calculator with a polished Bootstrap 5 UI, history, memory, themes, and keyboard shortcuts. Backend exposes JSON APIs for evaluate/preview/history/memory.

## Features

- UI (Thymeleaf/Bootstrap):
  - Basic and scientific keys with a collapsible Scientific panel (persisted).
  - Advanced functions: sin, cos, tan, sqrt, log (base 10), ln, abs, pow, constants pi/e, factorial n!.
  - Mini-plot: enter an expression with variable x (e.g., `sin(x)`) to see a small live graph next to the result.
  - History with search and filters, favorites, copy/reuse.
  - Memory keys: MC/MR/M+/M-.
  - Theme (light/dark/auto) and style modes (Flat, Glass, Soft Shadows), persisted.
  - Keyboard shortcuts: digits/operators, Enter/Esc/Backspace, etc.

- Backend (Java 17):
  - Expression tokenizer → shunting-yard (RPN) → evaluator with domain checks for scientific functions.
  - Endpoints for evaluate, preview, history, memory.
  - Health endpoint at `/health`.

## Prerequisites

- Java 17+
- Maven 3.9+

## Run locally

From `procalc/`:

```bash
mvn -DskipTests package
java -jar target/procalc-0.0.1.jar
```

Visit http://localhost:8080

Notes:
- `server.port` is configurable and defaults to `8080`. It also honors `PORT` env automatically via `src/main/resources/application.properties`:
  `server.port=${PORT:8080}`

## Keyboard cheatsheet

- Digits 0–9 and `.` to type numbers
- Operators `+ - * / ^`
- Parentheses `(` `)` and comma `,` (comma is the separator for `pow(x,y)`)
- Enter or `=` to evaluate, Esc for AC, Backspace for CE
- Factorial `!`

## Scientific usage tips

- Unary functions (e.g., `sin`, `cos`, `tan`, `sqrt`, `log`, `ln`, `abs`) accept `f(x)` syntax.
- `pow(x,y)` or use `^` operator.
- Constants: `pi`, `e`.
- Factorial is postfix: `5!`.
- Graph mode: include `x` (e.g., `sin(x)`, `pow(x,2) + 1`). The mini-plot appears beside the result.

## API (JSON)

Base URL: `/api/v1`

- POST `/evaluate`
  - Body: `{ "expression": "2 + 3 * 4" }`
  - Response: `{ status: "OK", result: 14, formatted: "14" }` (fields may include RPN and other metadata)

- POST `/preview`
  - Body: `{ "expression": "2 + 3 *" }`
  - Response: `{ status: "OK" | "ERROR", result/ formatted/ message }

- GET `/history`
- POST `/history/clear`

- GET `/memory`
- POST `/memory`  Body: `{ "op": "MC|MR|M+|M-", "value": "<number as string>" }`

## Health

- GET `/health` → 200 OK with simple body to indicate readiness.

## Deploy to Render

1. Push your repository to GitHub/GitLab. The app root is `procalc/`.
2. Create a new Render Web Service and connect your repo.
3. Either set Root Directory to `procalc/` (recommended) or keep repo root and prefix commands with `cd procalc &&`.

If Root Directory = `procalc/`:
- Environment: Java (Java 17)
- Build Command:
  ```
  mvn -DskipTests package
  ```
- Start Command:
  ```
  java -jar target/procalc-0.0.1.jar
  ```
- Health Check Path: `/health`

If Root Directory = repo root:
- Build Command: `cd procalc && mvn -DskipTests package`
- Start Command: `cd procalc && java -jar target/procalc-0.0.1.jar`
- Health Check Path: `/health`

`server.port` is already wired to the `PORT` env on Render.

## Troubleshooting

- Build fails on Render:
  - Ensure Java 17 is selected.
  - Verify no duplicate controllers or syntax errors. See `procalc/src/main/java/.../controller/HealthController.java`.
  - Check `pom.xml` builds locally: `mvn -DskipTests package`.

- App starts but 404 on `/`:
  - Ensure Thymeleaf template `src/main/resources/templates/index.html` exists and controller maps `/`.

- Graph not showing:
  - Only expressions containing variable `x` enable the mini-plot.

## License

MIT (or your chosen license).
