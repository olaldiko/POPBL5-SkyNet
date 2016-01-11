//
//  serverSocketComm.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 7/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#ifndef serverSocketComm_h
#define serverSocketComm_h
/*
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include "msgBuffers.h"
#include "messageParser.h"

#define SRV_ADDRESS "srv1.skynet.olaldiko.mooo.com"
#define SRV_PORT 5000
typedef struct SSC_STAT {
	int serverSocket;
	struct sockaddr_in serverSocketStruct;
	struct sockaddr_in clientSocketStruct;
	int sockSize;
	char* buffer;
	int state;
}SSC_STAT, *SSC_PSTAT;

SSC_STAT serverSocketStat;

MSGBUFF SSC_serverSendBuffer;

int stationId;
void SSC_initServerConnection();
void* SSC_serverListenerThreadFunc(void* args);
void SSC_sendMsgToServer(PMESSAGE msg);
 */
#endif /* serverSocketComm_h */
