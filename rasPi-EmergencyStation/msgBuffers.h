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
#include "messageParser.h"
#include <pthread.h>
#include <assert.h>

typedef struct MSGQUEUE {
	PMESSAGE msg;
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



#endif /* msgBuffers_h */
