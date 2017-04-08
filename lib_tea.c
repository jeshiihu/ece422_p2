#include <stdio.h>
#include <stdlib.h>
#include  "helper_TEAEncryption.h"

#include "encrypt.c"
#include "decrypt.c"

/*
 * Class:     TEAEncryption
 * Method:    encrypt
 * Signature: ([J[J)V
 */
JNIEXPORT void JNICALL Java_TEAEncryption_encrypt
	(JNIEnv *env, jobject object, jintArray val, jintArray key)
{
	jboolean *is_copy = 0;
	// ensure that isccopy is 0 so it will change it in memory
	jint *val_prim = (jint *) (*env)->GetIntArrayElements(env, val, is_copy);
	jint *key_prim = (jint *) (*env)->GetIntArrayElements(env, key, is_copy);

	if (val_prim == NULL || key_prim == NULL){
		printf("Cannot obtain val or key array from JVM\n");
		exit(-1);
	}

	encrypt(val_prim, key_prim);
	// releasing the arrays copies back into memory 
	(*env)->ReleaseIntArrayElements(env, val, val_prim, 0);
}
/*
 * Class:     TEAEncryption
 * Method:    decrypt
 * Signature: ([J[J)V
 */
JNIEXPORT void JNICALL Java_TEAEncryption_decrypt
	(JNIEnv *env, jobject object, jintArray val, jintArray key)
{
	jboolean *is_copy = 0;
	// ensure that isccopy is 0 so it will change it in memory
	jint *val_prim = (jint *) (*env)->GetIntArrayElements(env, val, is_copy);
	jint *key_prim = (jint *) (*env)->GetIntArrayElements(env, key, is_copy);

	if (val_prim == NULL || key_prim == NULL){
		printf("Cannot obtain val or key array from JVM\n");
		exit(-1);
	}

	decrypt(val_prim, key_prim);
	// releasing the arrays copies back into memory 
	(*env)->ReleaseIntArrayElements(env, val, val_prim, 0);
}

