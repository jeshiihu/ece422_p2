# ece422_p2
Client Server Encryption, Project2 for ECE 422

This project introduces the idea of security with encryption. The main encryption algorithm used is the Tiny Encryption Algorithm (TEA). The encrption is used to make a secure communication connection between a server and multiple clients. 

The client and server agree upon a shared secret key (obtained using Diffie-Hellman, DH) and then the client logs in with their username and password. Once this is valid, encrypted files are transfered from the server to the client.

# Compiling the Program
On the terminal compile the source code. Please ensure you use the correct gcc line in the Makefile for either Linux machines or Mac OSX machines. The targets can be left blank (default linux target) or one of the following: clean, mac, or linux
```bash
. run.sh {target}
```
Please ensure that the environment variable JAVA_HOME has been properly set!
# Running the Programs
User Password Shadow Creator
---
There are multiple ways to run this program and it will prompt the user with options at the start.<br>
* [0] Create a new shadow file from unhashed.txt<br>
* [1] Create a new shadow file and manually add users<br>
* [2] Add users from unhashed.txt to existing shadow.txt<br>
* [3] Add users, manually, to existing shadow.txt<br>
```bash
java UserPwShadowCreator
```
Server
---
Prior to running the Server program, ensure that you have the following directory (should be included if you cloned this repo)
* serverFiles: folder where server searches for the file being requested (contains files to copy from)
```bash
java Server {port}
```
Client
---
Once you login successfully, you can begin to retrieve files from the server. At any time after this point, you may enter "finished" to complete exhange and exit. All files that have been received are placed in a directory with your username as its name.
```bash
java Client {hostname} {port}
```
