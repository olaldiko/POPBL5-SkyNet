//
//  tcpServerSocketComm.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 9/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#ifndef tcpServerSocketComm_h
#define tcpServerSocketComm_h

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include "msgBuffers.h"
#include "messageParser.h"

#define SSC_SRV_ADDRESS "srv1.skynet.olaldiko.mooo.com"
#define SSC_SRV_PORT 5000
#define SSC_SRV_BUFLEN 1024
typedef struct SSC_STAT {
	int serverSocket;
	struct sockaddr_in serverSocketStruct;
	struct hostent *serverInfo;
	struct sockaddr_in clientSocketStruct;
	int sockSize;
	char* buffer;
	int state;
	pthread_t listenThread;
	pthread_t sendThread;
}SSC_STAT, *SSC_PSTAT;

SSC_STAT serverSocketStat;

MSGBUFF SSC_serverSendBuffer;


void SSC_makeServerConnection();
void SSC_initServerConnection();
void SSC_sendMessageToServer(PMESSAGE msg);

void SSC_initServerConnThreads();
void* msgSenderThreadFunc(void* args);
void* msgListenerThreadFunc(void* args);

#endif /* tcpServerSocketComm_h */
