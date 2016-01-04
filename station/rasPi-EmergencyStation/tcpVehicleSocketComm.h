//
//  tcpVehicleSocketComm.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 9/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#ifndef tcpVehicleSocketComm_h
#define tcpVehicleSocketComm_h

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
#include "stationActions.h"


#define VSC_SRV_PORT 6000
#define VSC_MAXPENDING 10
#define VSC_SOCKBUF_LEN 512

typedef struct VSC_STAT {
	int serverSocket;
	struct sockaddr_in serverSocketStruct;
	struct hostent *serverInfo;
	int sockSize;
	char* buffer;
	int state;
	pthread_t listenThread;
	pthread_t sendThread;
}VSC_STAT, *VSC_PSTAT;

void VSC_initVehicleServer();
void VSC_acceptConnections();
void* clientHandlerThreadFunc(void* args);
int VSC_SendMessageToVehicle(PMESSAGE msg, SA_PVEHICLE_DATA vehicle);
VSC_STAT vehicleServerStat;

#endif /* tcpVehicleSocketComm_h */