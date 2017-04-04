# By default, the linux version compiler will run
# 	make
# 
# Inorder to compile for Mac OSX, please run:
# 	make mac
SHELL := /bin/bash
CC = gcc
INCLUDE = -I$(JAVA_HOME)/include/

all: linux

linux: common
	@echo '----- Compiling code for Linux -----'
#	$(CC) $(INCLUDE) $(INCLUDE)linux -shared -fpic -o libinsertionsort.so lib_insertionsort.c

mac: common
	@echo '----- Compiling code for Mac -----'
	$(CC) $(INCLUDE) $(INCLUDE)darwin/ -dynamiclib -o libtea.jnilib lib_tea.c

common:
	@echo
	javac *.java
	javah TEAEncryption

clean:
	-rm -f *.o *.so *.jnilib *.class linux mac

run:
	./linux
	./mac