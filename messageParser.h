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
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <semaphore.h>
#include "msgBuffers.h"
#include "vehicleSocketComm.h"

#define MSG_MAXLEN 500
#define MSG_IDSIZE 10
#define MSG_TYPESIZE 10


typedef struct MESSAGE{
    int source; //0-Server, 1-Vehicle
    int msgType;    //0-Incoming, 1-IncomingACK,, 2-Outgoing 3-OutgoingACK
    in_addr_t srcAddress;
    struct sockaddr_in clientSocket;
    int msgSize;
    char* fullMsg;
    char* id;
    char* dataType;
    char* data;
    int waitACK;
    sem_t ackSem;
    struct MESSAGE* ackMsg;
    int ackReceived;
    
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
