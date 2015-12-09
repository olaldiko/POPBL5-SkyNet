//
//  msgBuffers.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 6/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#ifndef msgBuffers_h
#define msgBuffers_h

#include <stdio.h>
#include <pthread.h>
#include <assert.h>
#include "messageParser.h"


typedef struct MSGQUEUE {
	struct MESSAGE *msg;
	struct MSGQUEUE *next;
}MSGQUEUE, *PMSGQUEUE;

typedef struct MSGBUFF {
	pthread_mutex_t mtx;
	pthread_cond_t empty;
	pthread_cond_t full;
	int cant;
	int maxVals;
	PMSGQUEUE head;
}MSGBUFF, *PMSGBUFF;

PMSGBUFF MB_initBuffer(int maxVals);
void MB_putMessage(PMSGBUFF buffer, struct MESSAGE *msg);
struct MESSAGE* MB_getMessage(PMSGBUFF buffer);

#endif /* msgBuffers_h */
