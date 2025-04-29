# TCP Math-Service Application

A simple Java-based TCP client/server application that performs basic arithmetic operations.

## Project Overview

This repository contains:

- **TCPServer.java**  
  A multithreaded TCP server listening on port 6789. Clients connect, send a `JOIN <name>` handshake, then up to three math commands (`ADD`, `SUB`, `MUL`, `DIV`), and finally `EXIT`. All activity is logged to `logs/client_logs.txt`.

- **TCPClient.java**  
  A console client that connects to the server, sends your name, sends three hard-coded operations, reads and prints the results, then requests `EXIT`.

- **Makefile**  
  Automates compile, run (in a UNIX-style shell), and cleanup.

- **build.ps1 & run.ps1**  
  PowerShell scripts to compile and run on Windows without `make`.

- **logs/**  
  Directory (created at runtime) where the server writes connection, request/response, and disconnection records.

---

## Prerequisites

1. **Java Development Kit (JDK) 11 or later**  
   Ensure `javac` and `java` are on your `PATH`.

2. **Choice of build/run method**  
   - **UNIX-style shell** (Linux, macOS, or Windows with Git Bash, MSYS2, or WSL) + `make`  
 

## Option A: Build & Run with Makefile (Git Bash)

1. **Open** your Bash shell (Git Bash).  
2. **Navigate** to the `src/` directory:
   ```bash
   cd /c/Users/User/Desktop/CS4390/CNProject/MathServerProject/src
   ```
3. **Compile** both classes:
   ```bash
   make
   ```
4. **Run** server + client operations:
   ```bash
   make run
   ```
   - Starts the server in the background
   - Pipes in:
     ```
     JOIN Tousif
     ADD 2 2
     MUL 3 4
     DIV 10 5
     ```
   - Prints results in your terminal
   - Automatically shuts down the server when done

5. **Clean** compiled files and logs:
   ```bash
   make clean
   ```

---

## What You’ll See

- **Server console**  
  ```
  Server is UP and running on port 6789!
  ```
- **Client console**  
  ```
  JOIN Tousif
  WELCOME Tousif
  ADD 2 2
  Result: 4
  MUL 3 4
  Result: 12
  DIV 10 5
  Result: 2
  EXIT
  GOODBYE Tousif
  ```
- **logs/client_logs.txt**  
  ```
  Client 127.0.0.1 connected at Tue Apr 29 15:00:00 CDT 2025
  Client 127.0.0.1/Tousif requested: "JOIN Tousif" -> Response: "WELCOME Tousif" at Tue Apr 29 15:00:00 CDT 2025
  Client 127.0.0.1/Tousif requested: "ADD 2 2" -> Response: "4" at Tue Apr 29 15:00:01 CDT 2025
  …etc…
  Client 127.0.0.1/Tousif disconnected at Tue Apr 29 15:00:03 CDT 2025 (connected for 0m 3s)
  ```

---
