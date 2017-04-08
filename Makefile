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
#	$(CC) $(INCLUDE) $(INCLUDE)linux/ -shared -fpic -o helper/libtea.so helper/lib_tea.c
	$(CC) $(INCLUDE) $(INCLUDE)linux/ -shared -fpic -o libtea.so lib_tea.c

mac: common
#	$(CC) $(INCLUDE) $(INCLUDE)darwin/ -dynamiclib -o helper/libtea.jnilib helper/lib_tea.c
	$(CC) $(INCLUDE) $(INCLUDE)darwin/ -dynamiclib -o libtea.jnilib lib_tea.c

common:
	javac helper/*.java
	javac *java

	javah TEAEncryption

clean:
	-rm -f *.o *.so *.jnilib *.class linux mac
	-rm -f client/*.o client/*.so client/*.jnilib client/*.class linux mac
	-rm -f helper/*.o helper/*.so helper/*.jnilib helper/*.class linux mac


run:
	./linux
	./mac