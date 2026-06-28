# TaskFlow Engine

A console-based task management system built in pure Java, designed around custom-built linked data structures rather than relying on the built-in Collections framework. The project demonstrates how a Singly Linked List and a FIFO Queue can be engineered, extended, and wired into a layered application — complete with disk persistence and flexible search.

This started as an exploration of how far a "data structures fundamentals" exercise can be pushed toward something resembling a real, maintainable application: typed domain models, a service layer, a persistence layer, and a clean separation between business logic and console I/O.

---

## ✨ Core Features

### Task Management
- **Polymorphic task model** — a `Taskable` interface backed by an abstract `AbstractTask`, specialized into `PersonalTask`, `WorkTask`, and `AcademicTask`, each carrying its own domain-specific fields.
- Add, remove (from either end), and inspect tasks.
- Priority levels (`HIGH` / `MEDIUM` / `LOW`) modeled as a type-safe `enum`, not a raw `String`.
- Track completion status and deadlines using `java.time.LocalDate`.

### Smart Search
- **Exact-match lookup** by title.
- **Partial / fuzzy search** — a substring, case-insensitive query matches across both the title *and* description. Searching `"report"` will surface a task titled `"Quarterly Report Draft"` without requiring the full exact title.

### Reminders & Insights
- Upcoming-deadline reminders (configurable window, default 3 days).
- High-priority task reminders.
- Overdue task detection (deadline passed, not completed).
- Aggregate counts: by priority, completed tasks, urgent tasks.

### Urgent Task Queue
- A dedicated FIFO queue (`LinkedQueue`) for escalating tasks that need immediate attention, fully decoupled from the main task list.
- Search inside the urgent queue independently of the main list.
- Filter the urgent queue to high-priority items only, without mutating the original queue.

### Data Persistence *(new)*
- Tasks are automatically saved to a local file (`tasks.db`) and reloaded on startup — nothing is lost between runs.
- Custom pipe-delimited serialization format, written without any external library dependency, keeping the project dependency-free and easy to read for debugging.
- IDs are preserved across save/reload cycles via an internal sequence generator that re-synchronizes itself on load.

---

## 🛠 Tech Stack

| Layer            | Technology                          |
|-------------------|--------------------------------------|
| Language          | Java 17+                            |
| Build             | Maven (single-module, dependency-free) |
| Data Structures   | Custom Singly Linked List & Linked Queue (no `java.util.LinkedList`) |
| Persistence       | Flat-file storage via `java.nio.file` |
| Interface         | Console / CLI (`Scanner`-driven menu) |

No external libraries are used for the core logic — the linked list, the queue, and the persistence layer are all implemented from scratch to keep the data-structure fundamentals front and center.
com.taskflow

├── model/          → Taskable interface, AbstractTask, and its 3 subtypes, Priority enum

├── structures/      → Node<E>, SinglyLinkedList<E>, LinkedQueue<E>, LinkedCollection<E> contract

├── persistence/     → TaskRepository (save/load to disk)

├── service/          → TaskService, UrgentQueueService (business logic, search, filtering)

└── app/              → TaskFlowApplication (entry point), ConsoleView, TaskInputReader
The split keeps each layer testable in isolation:
- **`structures`** knows nothing about tasks — it's a generic, reusable library.
- **`service`** knows nothing about the console — it only depends on `structures` and `model`.
- **`app`** is the only layer that talks to `System.in` / `System.out`.

---

## 📊 Data Structures & Complexity Analysis

### Singly Linked List (`SinglyLinkedList<E>`)

Backs the main task collection. Maintains both `head` and `tail` pointers plus a running size counter, which is what makes several operations faster than a textbook minimal implementation.

| Operation                | Complexity | Notes |
|---------------------------|------------|-------|
| `addFirst(e)`             | O(1)       | Direct head pointer update |
| `addLast(e)`              | O(1)       | Tail pointer avoids full traversal |
| `removeFirst()`           | O(1)       | Direct head pointer update |
| `removeLast()`            | O(n)       | Singly-linked nodes have no `previous` pointer, so reaching the second-to-last node requires a scan |
| `first()` / `last()`      | O(1)       | Cached references |
| `size()` / `isEmpty()`    | O(1)       | Maintained counter, not recomputed |
| `findFirstMatch(p)`       | O(n)       | Linear scan, early-exits on first match |
| `findAllMatches(p)`       | O(n)       | Full scan, used by partial search |
| `countMatching(p)`        | O(n)       | Full scan |
| `removeMatching(p)`       | O(n)       | Scan + pointer relinking |

**Why a linked list over an array/ArrayList?** Task insertion is append-heavy (`addLast`) and removal from the front is common (processing tasks in order). Both are O(1) here, whereas an array-backed list would need to shift elements on front-removal. The trade-off accepted is O(n) random access, which this domain never needs — tasks are always accessed by traversal or search, never by index.

### Linked Queue (`LinkedQueue<E>`)

Backs the urgent-task queue. A dedicated structure rather than reusing the list, because a queue's contract (strict FIFO, no arbitrary insertion/removal) is narrower and should be enforced by the type itself.

| Operation                | Complexity | Notes |
|---------------------------|------------|-------|
| `enqueue(e)`              | O(1)       | Tail-pointer append |
| `dequeue()`               | O(1)       | Head-pointer removal |
| `peek()`                  | O(1)       | |
| `size()` / `isEmpty()`    | O(1)       | Maintained counter |
| `findFirstMatch(p)`       | O(n)       | Non-destructive scan (no dequeue/enqueue cycling) |
| `countMatching(p)`        | O(n)       | |
| `filter(p)`               | O(n)       | Builds a new queue, original is untouched |

**Design note:** the original approach to "search inside a queue" is to `dequeue` every element, inspect it, and `enqueue` it back — which works, but is wasteful and risks bugs if an exception interrupts the cycle. `LinkedQueue` instead implements `Iterable<E>`, allowing read-only traversal via a standard `for` loop without ever mutating the queue's structure. This is the single biggest internal redesign over a naive queue implementation.

### Persistence Layer

| Operation     | Complexity | Notes |
|----------------|------------|-------|
| `save(list)`  | O(n)       | One pass to serialize, one bulk disk write |
| `load()`      | O(n)       | One pass to read and reconstruct objects |

---

## 🚀 How to Run

### Prerequisites
- JDK 17 or later
- Maven (optional — a plain `javac`/`java` workflow also works)

### Option A — Maven
```bash
git clone https://github.com/<your-username>/TaskFlow-Engine.git
cd TaskFlow-Engine
mvn package
java -jar target/taskflow-engine.jar
```

### Option B — Plain javac
```bash
git clone https://github.com/<your-username>/TaskFlow-Engine.git
cd TaskFlow-Engine
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.taskflow.app.TaskFlowApplication
```

On first run, a `tasks.db` file is created in the working directory to persist your tasks between sessions. Delete it to start with a clean slate.

---

## 🗺 Possible Extensions

- Swap the flat-file store for a `JSON` (Jackson/Gson) or `SQLite` backend behind the same `TaskRepository` contract.
- Add a `Doubly` Linked List variant to bring `removeLast()` down to O(1).
- Wrap the service layer with a small REST API (Spring Boot / Javalin) to turn this into a web-accessible service.
- Add unit tests (JUnit 5) for the `structures` package, since it has zero external dependencies and is fully deterministic.

---

## 📄 License

MIT — feel free to fork, learn from, or build on top of this project.
---

## 🏗 Architecture**
