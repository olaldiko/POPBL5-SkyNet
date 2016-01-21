/** @file messageParser.h Message parser function headers. */

#ifndef messageParser_h
#define messageParser_h

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <pthread.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <semaphore.h>
#include <unistd.h>
#include "structures.h"
#include "msgBuffers.h"
#include "tcpVehicleSocketComm.h"
#include "stationActions.h"
#define MSG_IDSIZE 10 /**< Max size of the ID field. */
#define MSG_TYPESIZE 50 /**< Max size of the type field. */

 
extern PMSGBUFF receivedMsgBuff;  /**< Mailbox for the messages received by the system. */
extern int parserState;  /**< Variable used to stop the threads when set. */


void MP_initParser();
void MP_initMsgStruc(PMESSAGE msg, int msgSize);
PMESSAGE MP_messageReceiver(MP_PRECEIVERSTR rec);
int  MP_parseMessage(PMESSAGE msg);
void MP_wipeMessage(PMESSAGE msg);

void* MP_ParserThread(void* args);
#endif /* serverParser_h */
