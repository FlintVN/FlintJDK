
MODULE_NAME         :=  java.base
JC                  :=  javac
MODULE_SOURCE_PATH  :=  Src
OUTPUT_DIR          :=  Bin

OPT                 :=  -g
JFLAGS              :=  -Xlint:all -XDstringConcat=inline --system=none

GREEN               :=  \033[0;32m
CYAN                :=  \033[0;36m
RESET               :=  \033[0m

all: jar

$(MODULE_NAME): Makefile
	@echo -e "$(CYAN)Compiling$(RESET)" $@
	@$(JC) $(OPT) $(JFLAGS) -d $(OUTPUT_DIR) --module $@ --module-source-path $(MODULE_SOURCE_PATH)

jar: $(MODULE_NAME) META-INF/MANIFEST.MF Makefile
	@jar --create --file $(OUTPUT_DIR)/$(MODULE_NAME).jar --manifest META-INF/MANIFEST.MF -C $(OUTPUT_DIR)/$(MODULE_NAME) "."
	@echo -e "$(GREEN)Created$(RESET)" $(MODULE_NAME).jar

clean:
	@rm -rf $(OUTPUT_DIR)
	@echo -e "$(GREEN)Clean complete$(RESET)"
