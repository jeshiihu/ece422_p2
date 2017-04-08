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
Prior to this, ensure that you have the following files/directories (should be included if you cloned this repo)
* unhashed.txt: text file containing the unhashed usernames and passwords
* clientFiles/: folder where the client will store the requested files from the server
* serverFiles/: folder where server searches for the file being requested

Server
---
```bash
java Server {hostname} {port}
```
Client
---
```bash
java Client {hostname} {port}
```
