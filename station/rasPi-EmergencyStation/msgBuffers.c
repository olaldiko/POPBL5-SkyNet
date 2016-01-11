//
//  msgBuffers.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 6/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "msgBuffers.h"


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


