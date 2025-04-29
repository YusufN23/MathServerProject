# ─── Configuration ───────────────────────────────────────────────────────────
JAVAC    := javac
JAVA     := java
SRC      := TCPServer.java TCPClient.java
LOG_DIR  := logs
CLIENT_NAME := Tousif

.PHONY: all compile run clean

# Default: compile both
all: compile

compile:
	$(JAVAC) $(SRC)

# Ensure logs directory exists
$(LOG_DIR):
	mkdir -p $(LOG_DIR)

# Runs server + client with three ops
run: all $(LOG_DIR)
	@echo ">> Starting server..."
	@$(JAVA) TCPServer & \
	SERVER_PID=$$!; \
	sleep 1; \
	echo ">> Running client operations..."; \
	# pipe name + 3 operations into the client:
	printf "$(CLIENT_NAME)\nADD 2 2\nMUL 3 4\nDIV 10 5\n" | $(JAVA) TCPClient; \
	echo ">> Shutting down server..."; \
	kill $$SERVER_PID

clean:
	rm -f *.class
	rm -rf $(LOG_DIR)
