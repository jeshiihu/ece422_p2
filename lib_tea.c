#include <stdio.h>
#include <stdlib.h>
#include  "TEAEncryption.h"

#include "encrypt.c"
#include "decrypt.c"

/*
 * Class:     TEAEncryption
 * Method:    encrypt
 * Signature: ([J[J)V
 */
JNIEXPORT void JNICALL Java_TEAEncryption_encrypt
	(JNIEnv *env, jobject object, jlongArray val, jlongArray key)
{
	jboolean *is_copy = 0;
	jsize val_size = (*env)->GetArrayLength(env, val);
	jsize key_size = (*env)->GetArrayLength(env, key);

	// ensure that isccopy is 0 so it will change it in memory
	jlong *val_prim = (jlong *) (*env)->GetLongArrayElements(env, val, is_copy);
	jlong *key_prim = (jlong *) (*env)->GetLongArrayElements(env, key, is_copy);

	if (val_prim == NULL || key_prim == NULL){
		printf("Cannot obtain val or key array from JVM\n");
		exit(-1);
	}

	encrypt(val_prim, key_prim);

	// releasing the arrays copies back into memory 
	(*env)->ReleaseLongArrayElements(env, val, val_prim, 0);
}
/*
 * Class:     TEAEncryption
 * Method:    decrypt
 * Signature: ([J[J)V
 */
JNIEXPORT void JNICALL Java_TEAEncryption_decrypt
	(JNIEnv *env, jobject object, jlongArray val, jlongArray key)
{
	jboolean *is_copy = 0;
	jsize val_size = (*env)->GetArrayLength(env, val);
	jsize key_size = (*env)->GetArrayLength(env, key);

	// ensure that isccopy is 0 so it will change it in memory
	jlong *val_prim = (jlong *) (*env)->GetLongArrayElements(env, val, is_copy);
	jlong *key_prim = (jlong *) (*env)->GetLongArrayElements(env, key, is_copy);

	if (val_prim == NULL || key_prim == NULL){
		printf("Cannot obtain val or key array from JVM\n");
		exit(-1);
	}

	decrypt(val_prim, key_prim);

	// releasing the arrays copies back into memory 
	(*env)->ReleaseLongArrayElements(env, val, val_prim, 0);
}

