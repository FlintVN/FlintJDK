
MODULE_NAME         :=  java.base
JC                  :=  javac
MODULE_SOURCE_PATH  :=  src
OUTPUT_DIR          ?=  bin

RUN_OPT             :=  
DEV_OPT             :=  -g
JFLAGS              :=  -Xlint:all -XDstringConcat=inline --system=none -encoding UTF-8

all: run jar

run: Makefile
	@echo Compiling $(MODULE_NAME) for runtime mode
	@$(JC) $(RUN_OPT) $(JFLAGS) -d $(OUTPUT_DIR)/run --module $(MODULE_NAME) --module-source-path $(MODULE_SOURCE_PATH)

dev: Makefile
	@echo Compiling $(MODULE_NAME) for development mode
	@$(JC) $(DEV_OPT) $(JFLAGS) -d $(OUTPUT_DIR)/dev --module $(MODULE_NAME) --module-source-path $(MODULE_SOURCE_PATH)

jar: dev META-INF/MANIFEST.MF Makefile
	@echo Creating $(OUTPUT_DIR)/dev/$(MODULE_NAME).jar
	@cp -r $(MODULE_SOURCE_PATH)/* $(OUTPUT_DIR)/dev/$(MODULE_NAME)/src
	@jar --create --file $(OUTPUT_DIR)/dev/$(MODULE_NAME).jar --manifest META-INF/MANIFEST.MF -C $(OUTPUT_DIR)/dev/$(MODULE_NAME) "."

clean:
	@rm -rf $(OUTPUT_DIR)
	@echo Clean complete
