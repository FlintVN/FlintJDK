
MODULES         	:=  java.base flint.io flint.net flint.utils
JC                  :=  javac
MODULE_SOURCE_PATH  :=  src
OUT_DIR          	?=  bin

RUN_OPT             :=
DEV_OPT             :=  -g
JFLAGS              :=  -Xlint:all -XDstringConcat=inline --system=none -encoding UTF-8

RUN_DIR				:=	$(OUT_DIR)/run
DEV_DIR				:= 	$(OUT_DIR)/dev
JAR_DIR				:= 	$(OUT_DIR)/jar

all: run dev jar

$(JAR_DIR):
	@mkdir -p $(JAR_DIR)

run: $(foreach m,$(MODULES),$(RUN_DIR)/$(m))

dev: $(foreach m,$(MODULES),$(DEV_DIR)/$(m))

jar: $(foreach m,$(MODULES),$(JAR_DIR)/$(m).jar)

$(RUN_DIR)/%:
	@echo Compiling $* for runtime mode
	@$(JC) $(RUN_OPT) $(JFLAGS) -d $(RUN_DIR) --module $* --module-source-path $(MODULE_SOURCE_PATH)

$(DEV_DIR)/%:
	@echo Compiling $* for development mode
	@$(JC) $(DEV_OPT) $(JFLAGS) -d $(DEV_DIR) --module $* --module-source-path $(MODULE_SOURCE_PATH)

$(JAR_DIR)/%.jar: $(JAR_DIR) $(DEV_DIR)/%
	@cp -r $(MODULE_SOURCE_PATH)/$* $(DEV_DIR)/$*/src
	@jar --create --file $(JAR_DIR)/$*.jar --manifest META-INF/MANIFEST.MF -C $(OUT_DIR)/dev/$* "."
	@echo Created $(JAR_DIR)/$*.jar

clean:
	@rm -rf $(OUT_DIR)
	@echo Clean complete
