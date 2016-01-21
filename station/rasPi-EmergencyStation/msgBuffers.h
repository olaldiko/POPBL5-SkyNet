/** @file msgBuffers.h File containing the headers of all functions related to the message mailboxes. */
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
