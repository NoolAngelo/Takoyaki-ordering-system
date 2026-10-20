# Gelo's Takoyaki Ordering System

A small restaurant ordering system with two interfaces:

- Web POS built with HTML, CSS, and JavaScript
- Java console ordering application

The web app supports menu browsing, stock tracking, discounts, customer details,
checkout, receipts, and local order history.

## Run the Web App

Open `index.html` directly, or start a local server:

```bash
python3 -m http.server 8000
```

Open http://localhost:8000 in a browser.

To use the included smoke-test script:

```bash
./test_web.sh
```

## Run the Java App

Requirements: Java 11 or newer.

```bash
./run.sh
```

Or run it directly:

```bash
javac TakoyakiOrderingSystem.java
java TakoyakiOrderingSystem
```

## Main Features

- Takoyaki, combo, and drink menus
- Stock limits and low-stock indicators
- Quantity changes and order removal
- `STUDENT10`, `SENIOR15`, and `FIRST20` discounts
- Cash, card, and GCash payment options
- Printable receipts
- Browser order history using local storage
- Responsive layout for desktop and mobile screens

## Project Files

| File                          | Purpose                            |
| ----------------------------- | ---------------------------------- |
| `index.html`                  | Web application markup             |
| `styles.css`                  | Web application styling            |
| `script.js`                   | Web application behavior and state |
| `TakoyakiOrderingSystem.java` | Console application                |
| `run.sh`                      | Java compile and launch script     |
| `test_web.sh`                 | Web file smoke test                |

## Notes

The web app is client-side only. Orders are stored in the browser's local
storage and are not shared between browsers or devices.

## License

MIT
