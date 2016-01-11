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
#include "structures.h"
#include "messageParser.h"


PMSGBUFF MB_initBuffer(int maxVals);
void MB_putMessage(PMSGBUFF buffer, struct MESSAGE *msg);
PMESSAGE MB_getMessage(PMSGBUFF buffer);

#endif /* msgBuffers_h */
