//
//  serverSocketComm.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 7/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//
/*
#include "serverSocketComm.h"

void SSC_initServerConnection() {
	struct hostent *he;
	serverSocketStat.buffer = calloc(500, sizeof(char));
	serverSocketStat.sockSize = sizeof(struct sockaddr_in);
	he = gethostbyname(SRV_ADDRESS);
	if ((serverSocketStat.serverSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1) {
		perror("Error opening socket so server");
	} else {
		serverSocketStat.serverSocketStruct.sin_addr.s_addr = htonl(INADDR_ANY);
		serverSocketStat.serverSocketStruct.sin_port = 0;
		if (bind(serverSocketStat.serverSocket, (struct sockaddr*)&serverSocketStat.serverSocketStruct, sizeof(struct sockaddr_in)) == -1) {
			perror("Error binding to port");
		} else {
			serverSocketStat.clientSocketStruct.sin_addr.s_addr = he->h_addr_list[0];
			serverSocketStat.clientSocketStruct.sin_port = htons(SRV_PORT);
			serverSocketStat.clientSocketStruct.sin_family = AF_INET;
			sprintf(serverSocketStat.buffer, "\x02P\x02CON\x02%i\x02", stationId);
			sendto(serverSocketStat.serverSocket, serverSocketStat.buffer, strlen(serverSocketStat.buffer), 0, (struct sockaddr *)&serverSocketStat.clientSocketStruct, sizeof(struct sockaddr_in));
			memset(serverSocketStat.buffer, '\x00', 500);
			serverSocketStat.state = 1;
		}
	}
}

void* SSC_serverListenerThreadFunc(void* args) {
	PMESSAGE msg;
	int state = 1;
	int recv_length = 0;
	while (state) {
		if((recv_length = recvfrom(serverSocketStat.serverSocket, serverSocketStat.buffer, SRV_MAXBUFF, 0, (struct sockaddr*)&serverSocketStat.clientSocketStruct, &serverSocketStat.sockSize)) == -1) {
			state = -1;
		} else {
			msg = calloc(1, sizeof(MESSAGE));
			MP_initMsgStruc(msg, recv_length);
			msg->source = 1;
			msg->msgSize = recv_length;
			strcpy(msg->fullMsg, vehicleServerStat.serverBuffer);
			MB_putMessage(receivedMsgBuff, msg);
		}
	}
	pthread_exit(NULL);
}

void SSC_sendMsgToServer(PMESSAGE msg) {
	sendto(serverSocketStat.serverSocket, msg->fullMsg, strlen(msg->fullMsg), 0, (struct sockaddr *)&serverSocketStat.clientSocketStruct, serverSocketStat.sockSize);
	
	if(msg->msgType == 2) {
		if (msg->waitACK) {
			//TO-DO: Treat ACKs
		}
	}
}

void* serverSenderThreadFunc(void * args) {
	PMESSAGE msg;
	while (serverSocketStat.state == 1) {
		msg = MB_getMessage(&SSC_serverSendBuffer);
		SSC_sendMsgToServer(msg);
		free(msg);
	}
	pthread_exit(NULL);
}
 
 */


