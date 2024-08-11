
OUTPUT_DIR          :=  Bin

JC                  :=  javac

OPT                 :=  -g

JFLAGS              :=  -Xlint:all -XDstringConcat=inline -Xlint:-deprecation --system=none

MODULES             :=  java.base

MODULE_SOURCE_PATH  :=  Src

GREEN               :=  \033[0;32m
CYAN                :=  \033[0;36m
RESET               :=  \033[0m

all: $(MODULES)

$(MODULES):
	@echo -e "$(CYAN)Compiling$(RESET)" $@
	@$(JC) $(OPT) $(JFLAGS) -d $(OUTPUT_DIR) --module $@ --module-source-path $(MODULE_SOURCE_PATH)

clean:
	@rm -rf $(OUTPUT_DIR)
	@echo -e "$(GREEN)Clean complete$(RESET)"
