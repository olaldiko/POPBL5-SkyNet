//
//  tcpVehicleSocketComm.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 9/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "tcpVehicleSocketComm.h"

void VSC_initVehicleServer() {
	int waitTime = 1;
	
	vehicleServerStat.serverSocketStruct.sin_family = AF_INET;
	vehicleServerStat.serverSocketStruct.sin_port = htons(VSC_SRV_PORT);
	vehicleServerStat.serverSocketStruct.sin_addr.s_addr = htonl(INADDR_ANY);
	vehicleServerStat.sockSize = sizeof(struct sockaddr_in);
	while ((vehicleServerStat.serverSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP) < 0)) {
		printf("Error creating the vehicle server socket\n");
		sleep(waitTime);
		waitTime <<=1;
	}
	
	waitTime = 1;
	
	while (bind(vehicleServerStat.serverSocket, (struct sockaddr*)&vehicleServerStat.serverSocketStruct, (socklen_t)vehicleServerStat.sockSize) < 0) {
		printf("Error binding vehicle server socket\n");
		waitTime <<=1;
	}
	waitTime = 1;
	
	while (listen(vehicleServerStat.serverSocket, VSC_MAXPENDING) < 0) {
		printf("Error when listening connections in vehicle server socket\n");
		waitTime <<=1;
	}
}

void VSC_acceptConnections() {
	int clientSock;
	pthread_t *thread;
	while (accept(vehicleServerStat.serverSocket, (struct sockaddr*)&vehicleServerStat.serverSocketStruct, (socklen_t*)&vehicleServerStat.sockSize)) {
		thread = calloc(1, sizeof(pthread_t));
		pthread_create(thread, NULL, clientHandlerThreadFunc, (void *)clientSock);
	}
}

void* clientHandlerThreadFunc(void* args) {
	int clientSock = (int)args;
	int msgLength = 0;
	char* msgBuff;
	char* stxPos;
	char* etxPos;
	int inMsg = 0;
	int stxFound = 0;
	PMESSAGE msg;
	struct sockaddr_in* clientSocketStruct;
	int sockSize = sizeof(struct sockaddr_in);
	
	while ((msgLength = (int)recv(clientSock, msgBuff, VSC_SOCKBUF_LEN, 0)) != -1) {
		if (msgLength < VSC_SOCKBUF_LEN && inMsg == 0) {
			msg = calloc(1, sizeof(MESSAGE));
			MP_initMsgStruc(msg, msgLength);
			strcpy(msg->fullMsg, vehicleServerStat.buffer);
			memset(vehicleServerStat.buffer, 0, VSC_SOCKBUF_LEN);
			MB_putMessage(receivedMsgBuff, msg);
			memset(vehicleServerStat.buffer, 0, VSC_SOCKBUF_LEN);
		} else {
			if (inMsg == 0 && stxFound == 0) {
				if((stxPos = strchr(vehicleServerStat.buffer, '\x02')) != NULL) {	//If STX found, copy socket buffer to inner buffer and start reading
					inMsg = 1;
					msgBuff = calloc(VSC_SOCKBUF_LEN, sizeof(char));
					strcpy(msgBuff, stxPos+1);
					memset(vehicleServerStat.buffer, 0, VSC_SOCKBUF_LEN);
					stxFound = 1;
				} else {
					memset(vehicleServerStat.buffer, 0, VSC_SOCKBUF_LEN);				// If not STX found and not in MSG, discard the Junk
				}
			} else if(inMsg == 1 && stxFound == 1) {								//If we are reading a message
				if ((etxPos = strchr(vehicleServerStat.buffer, '\x03')) != NULL) {   //If we found ETX, copy contents and end reading.
					realloc(msgBuff, ((strlen(msgBuff)*sizeof(char) + 1) + (etxPos-vehicleServerStat.buffer)));
					strncat(msgBuff, vehicleServerStat.buffer, ((etxPos-1) - vehicleServerStat.buffer));
					inMsg = 0;
					stxFound = 0;
					msg = calloc(1, sizeof(MESSAGE));
					MP_initMsgStruc(msg, sizeof(msgBuff));
					strcpy(msg->fullMsg, msgBuff);
					
					getpeername(clientSock, (struct sockaddr*)&clientSocketStruct, (socklen_t*)&sockSize);
					msg->clientSocket = clientSocketStruct;
					
					MB_putMessage(receivedMsgBuff, msg);
					
					free(msgBuff);
					//TO-DO: Close the connection and destroy? Or mantain open? Add the thread to msg struct?
					
				} else {															//Else copy all contents and continue reading.
					realloc(msgBuff, ((strlen(msgBuff)*sizeof(char) + 1) + VSC_SOCKBUF_LEN));
					strcat(msgBuff, vehicleServerStat.buffer);
				}
			}
		}
	}
	pthread_exit(NULL);
}
