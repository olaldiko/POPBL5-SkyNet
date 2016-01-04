//
//  messageParser.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 30/11/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

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
#include "msgBuffers.h"
#include "tcpVehicleSocketComm.h"
#include "stationActions.h"
#include <unistd.h>
#define MSG_MAXLEN 500
#define MSG_IDSIZE 10
#define MSG_TYPESIZE 10


typedef struct MESSAGE{
    int source; //0-Server, 1-Vehicle
    in_addr_t srcAddress;
	int clientSocket;
    struct sockaddr_in* clientSocketStruct;
	pthread_t handlingThread;
	int isFirstMsg;
    int msgSize;
    char* fullMsg;
    char* id;
    char* dataType;
    char* data;
	char* msgCounter;
}MESSAGE, *PMESSAGE;


struct MSGBUFF* receivedMsgBuff;

void MP_initMsgStruc(PMESSAGE msg, int msgSize);
int  MP_parseMessage(PMESSAGE msg);


void MP_parseRouteMessage(PMESSAGE msg);
void MP_parseServerAlert(PMESSAGE msg);
void MP_parseServerACK(PMESSAGE msg);
void MP_parseServerNACK(PMESSAGE msg);

void MP_parseVehicleID(PMESSAGE msg);
void MP_parseVehicleIDRequest(PMESSAGE msg);
void MP_parseVehicleLocation(PMESSAGE msg);
void MP_parseVehicleStat(PMESSAGE msg);
void MP_parseVehicleACK(PMESSAGE msg);
void MP_parseVehicleNACK(PMESSAGE msg);

void MP_wipeMessage(PMESSAGE msg);
#endif /* serverParser_h */
