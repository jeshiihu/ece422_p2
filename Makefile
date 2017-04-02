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
#	$(CC) $(INCLUDE)include/ $(INCLUDE)darwin/ -dynamiclib -o libinsertionsort.jnilib lib_insertionsort.c

common:
	@echo '----- common -----'
	
	javac *.java
#	javah SecondaryInsertionSort
clean:
	-rm -f *.o *.so *.jnilib *.class linux mac

run:
	./linux
	./mac