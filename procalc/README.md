# ProCalc

Spring Boot + Thymeleaf scientific calculator with a polished Bootstrap 5 UI, history, memory, themes, and keyboard shortcuts.  
Backend exposes JSON APIs for evaluate, preview, history, and memory.

---

## ✨ Features

### UI (Thymeleaf/Bootstrap)
- Basic and scientific keys with a collapsible Scientific panel (state persisted).
- Advanced functions: `sin`, `cos`, `tan`, `sqrt`, `log` (base 10), `ln`, `abs`, `pow`, constants `pi` / `e`, factorial `n!`.
- Mini-plot: enter an expression with variable `x` (e.g., `sin(x)`) to see a live graph next to the result.
- History with search, filters, favorites, copy/reuse.
- Memory keys: `MC` / `MR` / `M+` / `M-`.
- Theme options: light / dark / auto, and style modes (Flat, Glass, Soft Shadows), persisted.
- Keyboard shortcuts for digits, operators, Enter / Esc / Backspace, etc.

### Backend (Java 17)
- Expression tokenizer → shunting-yard (RPN) → evaluator with domain checks for scientific functions.
- Endpoints for evaluate, preview, history, and memory.
- Health endpoint at `/health`.

---

## 🔧 Prerequisites

- Java 17+
- Maven 3.9+

---

## 🚀 Run locally

From `procalc/`:

```bash
mvn -DskipTests package
java -jar target/procalc-0.0.1.jar
````

Then open: [http://localhost:8080](http://localhost:8080)

**Notes:**

* `server.port` is configurable and defaults to `8080`.
* It also honors the `PORT` environment variable automatically via `src/main/resources/application.properties`:

  ```properties
  server.port=${PORT:8080}
  ```

---

## ⌨️ Keyboard Cheatsheet

* Digits `0–9` and `.` to type numbers
* Operators: `+ - * / ^`
* Parentheses `(` `)` and comma `,` (comma is the separator for `pow(x,y)`)
* Enter or `=` to evaluate, Esc for **AC**, Backspace for **CE**
* Factorial: `!`

---

## 📘 Scientific Usage Tips

* Unary functions (e.g., `sin`, `cos`, `tan`, `sqrt`, `log`, `ln`, `abs`) use `f(x)` syntax.
* Power: `pow(x,y)` or use `^`.
* Constants: `pi`, `e`.
* Factorial is postfix: `5!`.
* Graph mode: include `x` in the expression (e.g., `sin(x)`, `pow(x,2)+1`).
  The mini-plot appears beside the result.

---

## 🔌 API (JSON)

Base URL: `/api/v1`

* **POST** `/evaluate`
  **Body:**

  ```json
  { "expression": "2 + 3 * 4" }
  ```

  **Response:**

  ```json
  { "status": "OK", "result": 14, "formatted": "14" }
  ```

* **POST** `/preview`
  **Body:**

  ```json
  { "expression": "2 + 3 *" }
  ```

  **Response:**

  ```json
  { "status": "ERROR", "message": "Unexpected end of expression" }
  ```

* **GET** `/history`

* **POST** `/history/clear`

* **GET** `/memory`

* **POST** `/memory`
  **Body:**

  ```json
  { "op": "MC|MR|M+|M-", "value": "123.45" }
  ```

---

## 🩺 Health Check

* **GET** `/health` → returns `200 OK` with a simple body to indicate readiness.

---

## 🌐 Deploy to Render

1. Push your repository to GitHub/GitLab. The app root is `procalc/`.
2. Create a new Render Web Service and connect your repo.
3. Either:

   * Set **Root Directory** to `procalc/` (recommended), or
   * Keep repo root and prefix commands with `cd procalc &&`.

### If Root Directory = `procalc/`

* **Environment:** Java (Java 17)
* **Build Command:**

  ```bash
  mvn -DskipTests package
  ```
* **Start Command:**

  ```bash
  java -jar target/procalc-0.0.1.jar
  ```
* **Health Check Path:** `/health`

### If Root Directory = repo root

* **Build Command:**

  ```bash
  cd procalc && mvn -DskipTests package
  ```
* **Start Command:**

  ```bash
  cd procalc && java -jar target/procalc-0.0.1.jar
  ```
* **Health Check Path:** `/health`

**Note:** `server.port` is already wired to the `PORT` env variable on Render.

---

## 🛠️ Troubleshooting

* **Build fails on Render:**

  * Ensure Java 17 is selected.
  * Verify no duplicate controllers or syntax errors. See: `procalc/src/main/java/.../controller/HealthController.java`.
  * Check local build:

    ```bash
    mvn -DskipTests package
    ```

* **App starts but 404 on `/`:**

  * Ensure Thymeleaf template `src/main/resources/templates/index.html` exists.
  * Ensure controller maps `/`.

* **Graph not showing:**

  * Only expressions containing variable `x` enable the mini-plot.

---

## 📜 License

[MIT License](LICENSE) – feel free to use, modify, and distribute with attribution.


