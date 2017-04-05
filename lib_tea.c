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
	(JNIEnv *env, jobject object, jintArray val, jintArray key)
{
	jboolean *is_copy = 0;
	jsize val_size = (*env)->GetArrayLength(env, val);
	jsize key_size = (*env)->GetArrayLength(env, key);

	// ensure that isccopy is 0 so it will change it in memory
	jint *val_prim = (jint *) (*env)->GetIntArrayElements(env, val, is_copy);
	jint *key_prim = (jint *) (*env)->GetIntArrayElements(env, key, is_copy);

	if (val_prim == NULL || key_prim == NULL){
	printf("Cannot obtain val or key array from JVM\n");
	exit(-1);
	}

	jlongArray val_long_arr = (*env)->NewLongArray(env, val_size);
	jlong *val_long_prim = (jlong *) (*env)->GetLongArrayElements(env, val_long_arr, is_copy);

	for(int i=0; i < val_size; i++)
		val_long_prim[i] = (jlong)val_prim[i];

	jlongArray key_long_arr = (*env)->NewLongArray(env, key_size);
	jlong *key_long_prim = (jlong *) (*env)->GetLongArrayElements(env, key_long_arr, is_copy);
	
	for(int i=0; i < key_size; i++)
		key_long_prim[i] = (jlong)key_prim[i];

	// releasing the arrays copies back into memory 
	encrypt(val_long_prim, key_long_prim);

	//convert back
	for(int i=0; i < val_size; i++)
		val_prim[i] = (jint)val_long_prim[i];

	for(int i=0; i < key_size; i++)
		key_prim[i] = (jint)key_long_prim[i];

	(*env)->ReleaseIntArrayElements(env, val, val_prim, 0);
	(*env)->ReleaseIntArrayElements(env, key, key_prim, 0);
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
	jsize val_size = (*env)->GetArrayLength(env, val);
	jsize key_size = (*env)->GetArrayLength(env, key);

	// ensure that isccopy is 0 so it will change it in memory
	jint *val_prim = (jint *) (*env)->GetIntArrayElements(env, val, is_copy);
	jint *key_prim = (jint *) (*env)->GetIntArrayElements(env, key, is_copy);

	if (val_prim == NULL || key_prim == NULL){
	printf("Cannot obtain val or key array from JVM\n");
	exit(-1);
	}

	jlongArray val_long_arr = (*env)->NewLongArray(env, val_size);
	jlong *val_long_prim = (jlong *) (*env)->GetLongArrayElements(env, val_long_arr, is_copy);

	for(int i=0; i < val_size; i++)
		val_long_prim[i] = (jlong)val_prim[i];

	jlongArray key_long_arr = (*env)->NewLongArray(env, key_size);
	jlong *key_long_prim = (jlong *) (*env)->GetLongArrayElements(env, key_long_arr, is_copy);
	
	for(int i=0; i < key_size; i++)
		key_long_prim[i] = (jlong)key_prim[i];

	// releasing the arrays copies back into memory
	decrypt(val_long_prim, key_long_prim);

	//convert back
	for(int i=0; i < val_size; i++)
		val_prim[i] = (jint)val_long_prim[i];

	for(int i=0; i < key_size; i++)
		key_prim[i] = (jint)key_long_prim[i];

	(*env)->ReleaseIntArrayElements(env, val, val_prim, 0);
	(*env)->ReleaseIntArrayElements(env, key, key_prim, 0);
}

