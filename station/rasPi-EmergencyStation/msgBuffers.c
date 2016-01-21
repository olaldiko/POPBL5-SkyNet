/** @file msgBuffers.c File containing the definitions of all functions related to the message mailboxes. */

#include "msgBuffers.h"

/**
 * Create a new buffer or mailbox and initialize it for n values. 
 * This function creates a new buffer by allocating all the memory, mutexes and values needed and
 * returns the pointer to the newly created buffer.
 * @param maxVals The maximum number of values.
 * @return The pointer to the new buffer structure.
 **/
PMSGBUFF MB_initBuffer(int maxVals) {
	PMSGBUFF buffer = calloc(1, sizeof(MSGBUFF));
	assert(maxVals > 0);
	pthread_mutex_init(&buffer->mtx, NULL);
	pthread_cond_init(&buffer->empty, NULL);
	pthread_cond_init(&buffer->full, NULL);
	buffer->cant = 0;
	buffer->maxVals = maxVals;
	buffer->head = NULL;
	return buffer;
}

/**
 * Put a message into the buffer.
 * This function puts a message passed by parameter into the chosen buffer. 
 * If the buffer is full it will wait until there's space on it.
 * @param buffer The buffer to put the message into.
 * @param msg The message to put in the buffer.
 **/

void MB_putMessage(PMSGBUFF buffer, PMESSAGE msg) {
	PMSGQUEUE bufferCursor;
	assert(buffer != NULL);
	assert(msg != NULL);
	
	pthread_mutex_lock(&buffer->mtx);
	while (buffer->cant == buffer->maxVals) {
		pthread_cond_wait(&buffer->full, &buffer->mtx);
	}
	if(buffer->head == NULL) {
		buffer->head = calloc(1, sizeof(MSGQUEUE));
		buffer->head->msg = msg;
	} else {
		for (bufferCursor = buffer->head; bufferCursor->next != NULL; bufferCursor = bufferCursor->next);
		bufferCursor->next = calloc(1, sizeof(MSGQUEUE));
		bufferCursor->next->msg = msg;
	}
	buffer->cant++;
	pthread_cond_signal(&buffer->empty);
	pthread_mutex_unlock(&buffer->mtx);
}

/**
 * Get a message from the buffer.
 * This function will get a message from the selected buffer and return it's pointer. 
 * If the buffer is empty, it will wait for a value to be available.
 * @param buffer The buffer to get the message.
 * @return The pointer to the adquired message.
 **/

PMESSAGE MB_getMessage(PMSGBUFF buffer) {
	PMSGQUEUE bufferCursor;
	PMESSAGE retMsg;
	assert(buffer != NULL);
	
	pthread_mutex_lock(&buffer->mtx);
	while (buffer->cant == 0) {
		
		pthread_cond_wait(&buffer->empty, &buffer->mtx);
	}
	bufferCursor = buffer->head;
	buffer->head = buffer->head->next;
	retMsg = bufferCursor->msg;
	free(bufferCursor);
	assert(retMsg != NULL);
	buffer->cant--;
	pthread_cond_signal(&buffer->full);
	pthread_mutex_unlock(&buffer->mtx);
	return retMsg;
}


