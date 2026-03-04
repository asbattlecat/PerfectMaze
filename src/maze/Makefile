SRC_DIR = src/main/java
BUILD_DIR = build
CLASSES_DIR = $(BUILD_DIR)/classes
RUN_FILE = run.sh
LIB_DIR = lib
JAVAFX_LIB_DIR = $(LIB_DIR)/javafx

MAIN_CLASS = view.MazeApplication

JAVAFX_VERSION = 23.0.2
JAVAFX_MODULES = javafx.controls,javafx.fxml,javafx.graphics
JAVAFX_OS = linux

MAVEN_REPO = https://repo1.maven.org/maven2

TEST_DIR = src/test/java
TEST_BUILD_DIR = $(BUILD_DIR)/tests
JUNIT_JAR = $(LIB_DIR)/junit-platform-console-standalone-1.10.2.jar

JACOCO_JAR = $(LIB_DIR)/jacocoagent-0.8.12.jar
JACOCO_REPORT_DIR = $(BUILD_DIR)/jacoco-report
JACOCO_EXEC_FILE = $(BUILD_DIR)/jacoco.exec

DESTDIR ?= ./install

# === Цели ===
.PHONY: all install uninstall clean run dist tests dvi

###############################################################################
# all: компиляция
###############################################################################
all: check-javafx $(CLASSES_DIR)
	@echo "▶ Compiling sources..."
	javac -d $(CLASSES_DIR) \
		--module-path $(JAVAFX_LIB_DIR) \
		--add-modules $(JAVAFX_MODULES) \
		$(shell find $(SRC_DIR) -name "*.java")
	@echo "✓ Compiled to $(CLASSES_DIR)"

$(CLASSES_DIR):
	mkdir -p $(CLASSES_DIR)

###############################################################################
# install: установка в DESTDIR
###############################################################################
install: all
	@echo "▶ Installing to $(DESTDIR)..."
# 1. Создаём папку для установки
	mkdir -p $(DESTDIR)/$(LIB_DIR)
# 2. Копируем скомпилированные классы
	cp -r $(CLASSES_DIR)/* $(DESTDIR)/$(LIB_DIR)/
# 3. Создаём файл run.sh и записываем в него команды
#   Говорим: "Это скрипт для Linux"
	@echo '#!/bin/sh' > $(DESTDIR)/$(RUN_FILE)
#   Говорим: "Библиотеки лежат здесь"
	@echo 'JAVAFX_LIB_DIR="$(JAVAFX_LIB_DIR)"' >> $(DESTDIR)/$(RUN_FILE)
#   Говорим: "Код лежит здесь"
	@echo 'CLASSPATH="$$(dirname "$$0")/$(LIB_DIR)"' >> $(DESTDIR)/$(RUN_FILE)
#   Говорим: "Скрепи это всё запусти и команду java"
	@echo 'java -cp "$$CLASSPATH" --module-path "$$JAVAFX_LIB_DIR" --add-modules $(JAVAFX_MODULES) $(MAIN_CLASS)' >> $(DESTDIR)/run.sh
# 4. Делаем скрипт исполняемым
	chmod +x $(DESTDIR)/$(RUN_FILE)
	@echo "✓ Installed. Run with: $(DESTDIR)/$(RUN_FILE)"

###############################################################################
# run: установка и запуск
###############################################################################
run: install
	@echo "▶ Running application..."
	$(DESTDIR)/$(RUN_FILE)

###############################################################################
# uninstall: удаление
###############################################################################
uninstall: clean
	@echo "▶ Uninstalling $(DESTDIR)..."
	rm -rf $(DESTDIR)
	@echo "✓ Uninstalled"

clean:
	@echo "▶ Cleaning..."
	rm -rf $(BUILD_DIR)
	@echo "✓ Cleaned"

# Вспомогательная цель: проверить и скачать JavaFX
check-javafx:
	@if [ ! -d "$(JAVAFX_LIB_DIR)" ] || [ -z "$$(find $(JAVAFX_LIB_DIR) -maxdepth 1 -type f 2>/dev/null)" ]; then \
		echo "▶ JavaFX was not found. Loading..."; \
		$(MAKE) download-javafx; \
	fi

download-javafx:
	@echo "▶ Load JavaFX $(JAVAFX_VERSION) for $(JAVAFX_OS)..."
	@mkdir -p $(JAVAFX_LIB_DIR)
	@for module in base controls fxml graphics media swing web; do \
		echo "  Load javafx-$$module..."; \
		curl -sL -o $(JAVAFX_LIB_DIR)/javafx-$$module-$(JAVAFX_VERSION).jar \
			"$(MAVEN_REPO)/org/openjfx/javafx-$$module/$(JAVAFX_VERSION)/javafx-$$module-$(JAVAFX_VERSION)-$(JAVAFX_OS).jar" || \
		wget -q -O $(JAVAFX_LIB_DIR)/javafx-$$module-$(JAVAFX_VERSION).jar \
			"$(MAVEN_REPO)/org/openjfx/javafx-$$module/$(JAVAFX_VERSION)/javafx-$$module-$(JAVAFX_VERSION)-$(JAVAFX_OS).jar"; \
	done
	@echo "✓ JavaFX is loaded in $(JAVAFX_LIB_DIR)"

###############################################################################
# test: компиляция и запуск тестов
###############################################################################
.PHONY: test test-deps

tests: test-deps $(TEST_BUILD_DIR) all
	@echo "▶ Compiling tests..."
#   Компиляция тестов:
	javac -d $(TEST_BUILD_DIR) \
		-cp $(CLASSES_DIR):$(JUNIT_JAR) \
		$(shell find $(TEST_DIR) -name "*.java" 2>/dev/null)
	@echo "▶ Running tests..."
#   Запуск тестов:
	java -cp $(JUNIT_JAR):$(TEST_BUILD_DIR):$(CLASSES_DIR) \
		org.junit.platform.console.ConsoleLauncher execute \
		--scan-classpath=$(TEST_BUILD_DIR) \
		--include-classname=".*Tests?" \
		--details=tree
	@echo "✓ Tests completed"

$(TEST_BUILD_DIR):
	mkdir -p $(TEST_BUILD_DIR)

###############################################################################
# coverage: запуск тестов с измерением покрытия
###############################################################################
.PHONY: coverage coverage-report

coverage: test-deps $(TEST_BUILD_DIR) all $(JACOCO_REPORT_DIR)
	@echo "▶ Running tests with JaCoCo..."
#   Запускаем тесты с агентом JaCoCo:
	java -javaagent:$(JACOCO_JAR)=destfile=$(JACOCO_EXEC_FILE),output=file \
		-cp $(JUNIT_JAR):$(TEST_BUILD_DIR):$(CLASSES_DIR) \
		org.junit.platform.console.ConsoleLauncher execute \
		--scan-classpath=$(TEST_BUILD_DIR) \
		--include-classname=".*Tests?" \
		--details=tree
	@echo "✓ Tests completed. Coverage data saved to $(JACOCO_EXEC_FILE)"
	@$(MAKE) coverage-report

$(JACOCO_REPORT_DIR):
	mkdir -p $(JACOCO_REPORT_DIR)

coverage-report:
	@echo "▶ Generating coverage report..."
	@if [ ! -f "$(LIB_DIR)/jacococli-0.8.12.jar" ]; then \
		echo "JaCoCo CLI not found. Downloading..."; \
		curl -L -o $(LIB_DIR)/jacococli-0.8.12.jar $(MAVEN_REPO)/org/jacoco/org.jacoco.cli/0.8.12/org.jacoco.cli-0.8.12-nodeps.jar; \
	fi
#   Генерируем отчёт:
	java -jar $(LIB_DIR)/jacococli-0.8.12.jar report $(JACOCO_EXEC_FILE) \
		--classfiles $(CLASSES_DIR) \
		--sourcefiles $(SRC_DIR) \
		--html $(JACOCO_REPORT_DIR) \
		--name "Maze Project Coverage"
	@echo "✓ Report generated: $(JACOCO_REPORT_DIR)/index.html"

# Вспомогательная цель: проверить и скачать зависимости для тестов
test-deps:
	@echo "▶ Checking test dependencies..."
#   если файла JUnit ещё нет в папке lib/
#   скачиваем JUnit
	@if [ ! -f "$(JUNIT_JAR)" ]; then \
		@echo "⚠ JUnit 5 not found. Downloading..."; \
		@mkdir -p $(LIB_DIR); \
		curl -L -o $(JUNIT_JAR) $(MAVEN_REPO)/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar; \
	fi
#   если файла jacoco ещё нет в папке lib/
#   скачиваем jacoco
	@if [ ! -f "$(JACOCO_JAR)" ]; then \
		@echo "⚠ JaCoCo not found. Downloading..."; \
		@mkdir -p $(LIB_DIR); \
		curl -L -o $(JACOCO_JAR) $(MAVEN_REPO)/org/jacoco/org.jacoco.agent/0.8.12/org.jacoco.agent-0.8.12-runtime.jar; \
	fi
	@echo "✓ Test dependencies ready"

###############################################################################
# clang-format: соответствие стандартам Google Style
###############################################################################
format:
	@echo "▶ Check format..."
	@cp ../../materials/linters/.clang-format ../../src/.clang-format
	find . -type f \( -name "*.java" \) -exec clang-format -n {} +
	@rm -f .clang-format
	@echo "✓ Format check"

do_format:
	@echo "▶ Formating..."
	@cp ../../materials/linters/.clang-format ../../src/.clang-format
	find . -type f \( -name "*.java" \) -exec clang-format -i {} +
	@rm -f .clang-format
	@echo "✓ Formatted"