
# 🔢 ProCalc

Spring Boot + Thymeleaf **scientific calculator** with a polished **Bootstrap 5 UI**, history, memory, themes, and keyboard shortcuts.  
The backend exposes clean JSON APIs for evaluate/preview/history/memory.

---

## ✨ Features

### UI (Thymeleaf + Bootstrap 5)
- Basic and scientific keys with a **collapsible Scientific panel** (state persisted).
- Advanced functions: `sin`, `cos`, `tan`, `sqrt`, `log` (base 10), `ln`, `abs`, `pow`, constants `pi`/`e`, and factorial `n!`.
- **Mini-plot**: enter an expression with variable `x` (e.g., `sin(x)`) to see a small live graph next to the result.
- **History** with search, filters, favorites, copy/reuse.
- **Memory keys**: MC / MR / M+ / M−.
- **Themes**: Light, Dark, or Auto + style modes (Flat, Glass, Soft Shadows).
- **Keyboard shortcuts**: digits/operators, Enter/Esc/Backspace, factorial `!`.

### Backend (Java 17)
- Expression tokenizer → shunting-yard (RPN) → evaluator with domain checks for scientific functions.
- Endpoints for evaluate, preview, history, memory.
- Health endpoint at `/health`.

---

## 🔧 Prerequisites

- Java 17+
- Maven 3.9+

---

## ▶️ Run Locally

From the `procalc/` folder:

```bash
mvn -DskipTests package
java -jar target/procalc-0.0.1.jar
````

Then visit:
👉 [http://localhost:8080](http://localhost:8080)

Notes:

* `server.port` defaults to `8080`.
* It honors `PORT` automatically (see `src/main/resources/application.properties`):

  ```properties
  server.port=${PORT:8080}
  ```

---

## ⌨️ Keyboard Cheatsheet

* Digits `0–9` and `.` to type numbers
* Operators `+ - * / ^`
* Parentheses `(` `)` and comma `,` (comma for `pow(x,y)`)
* **Enter** or `=` → evaluate
* **Esc** → AC (clear all)
* **Backspace** → CE (clear entry)
* Factorial `!`

---

## 📘 Scientific Usage Tips

* Unary functions: `sin(x)`, `cos(x)`, `tan(x)`, `sqrt(x)`, `log(x)`, `ln(x)`, `abs(x)`.
* Exponent: `pow(x,y)` or `x ^ y`.
* Constants: `pi`, `e`.
* Factorial: postfix → `5!`.
* Graph mode: include variable `x` (e.g., `sin(x)`, `pow(x,2)+1`). The **mini-plot** appears next to the result.

---

## 📡 API (JSON)

Base URL: `/api/v1`

* **POST** `/evaluate`
  Body:

  ```json
  { "expression": "2 + 3 * 4" }
  ```

  Response:

  ```json
  { "status": "OK", "result": 14, "formatted": "14" }
  ```

* **POST** `/preview`
  Body:

  ```json
  { "expression": "2 + 3 *" }
  ```

  Response:

  ```json
  { "status": "ERROR", "message": "Unexpected end of expression" }
  ```

* **GET** `/history`

* **POST** `/history/clear`

* **GET** `/memory`

* **POST** `/memory`
  Body:

  ```json
  { "op": "M+", "value": "42" }
  ```

* **GET** `/health` → 200 OK with JSON metadata.

---

## 🌍 Deploy to Render

1. Push your repository to GitHub/GitLab. Root app folder: `procalc/`.
2. Create a new Render **Web Service**.
3. Configure:

**If Root Directory = `procalc/`:**

* Environment: Java 17
* Build Command:

  ```bash
  mvn -DskipTests package
  ```
* Start Command:

  ```bash
  java -jar target/procalc-0.0.1.jar
  ```
* Health Check Path: `/health`

**If Root Directory = repo root:**

* Build Command:

  ```bash
  cd procalc && mvn -DskipTests package
  ```
* Start Command:

  ```bash
  cd procalc && java -jar target/procalc-0.0.1.jar
  ```
* Health Check Path: `/health`

👉 `server.port` is already wired to `PORT` for Render.

---

## 🐞 Troubleshooting

* **Build fails on Render:**

  * Ensure Java 17 is selected.
  * Verify no duplicate controllers or stray syntax errors.
  * Build locally with `mvn -DskipTests package`.

* **App starts but 404 on `/`:**

  * Confirm `src/main/resources/templates/index.html` exists.
  * Ensure a controller maps `/`.

* **Graph not showing:**

  * Mini-plot only appears when expression includes variable `x`.

---

## 📜 License

MIT (or your chosen license).

---

## 👨‍💻 Author

* Built by **Prajwal R K**
* GitHub: [Prajwal-R-K](https://github.com/Prajwal-R-K/calculator-button-app.git)
* PortFolio: [Prajwal R K](https://prajwal-r-k.github.io/PortFolio).

