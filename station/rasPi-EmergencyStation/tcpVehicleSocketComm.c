//
//  tcpVehicleSocketComm.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 9/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "tcpVehicleSocketComm.h"

VSC_STAT vehicleServerStat;

void VSC_initVehicleServer() {
	int waitTime = 1;
	vehicleServerStat.state = 1;
	vehicleServerStat.serverSocketStruct.sin_family = AF_INET;
	vehicleServerStat.serverSocketStruct.sin_port = htons(VSC_SRV_PORT);
	vehicleServerStat.serverSocketStruct.sin_addr.s_addr = htonl(INADDR_ANY);
	vehicleServerStat.sockSize = sizeof(struct sockaddr_in);
	while ((vehicleServerStat.serverSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
		perror("Error creating the vehicle server socket");
		sleep(waitTime);
		waitTime <<=1;
	}
	
	waitTime = 1;
	
	while (bind(vehicleServerStat.serverSocket, (struct sockaddr*)&vehicleServerStat.serverSocketStruct, (socklen_t)vehicleServerStat.sockSize) < 0) {
		perror("Error binding vehicle server socket");
		waitTime <<=1;
	}
	waitTime = 1;
	
	while (listen(vehicleServerStat.serverSocket, VSC_MAXPENDING) < 0) {
		perror("Error when listening connections in vehicle server socket");
		waitTime <<=1;
	}
	VSC_acceptConnections();
}

void VSC_acceptConnections() {
	int clientSock;
	pthread_t *thread;
	while ((clientSock = accept(vehicleServerStat.serverSocket, (struct sockaddr*)&vehicleServerStat.serverSocketStruct, (socklen_t*)&vehicleServerStat.sockSize))) {
		if (clientSock < 0) {
			perror("VSC Error accepting connexion");
		}
		thread = calloc(1, sizeof(pthread_t));
		pthread_create(thread, NULL, VSC_inboundHandlerThreadFunc, (void *)clientSock);
	}
}
void VSC_shutdownVehicleServer() {
	vehicleServerStat.state = 0;
	SA_PVEHICLE_DATA vehicle;
	for (int i = 0; i < SA_countVehiclesInList(); i++) {
		vehicle = SA_getVehicleByIndex(i);
		shutdown(vehicle->clientSocket, SHUT_RDWR);
		close(vehicle->clientSocket);
		pthread_join(vehicle->inboxThread, NULL);
		pthread_join(vehicle->outboxThread, NULL);
	}
	shutdown(vehicleServerStat.serverSocket, SHUT_RDWR);
	close(vehicleServerStat.serverSocket);
	pthread_join(vehicleServerStat.listenThread, NULL);
}


void* VSC_inboundHandlerThreadFunc(void* args) {
	int clientSock = (int)args;
	int msgLength = 0;
	char *clientBuff = calloc(VSC_SOCKBUF_LEN, sizeof(char));
	char* msgBuff = calloc(VSC_SOCKBUF_LEN, sizeof(char));
	char* stxPos;
	char* etxPos;
	int inMsg = 0;
	int stxFound = 0;
	int firstMessage = 1;
	PMESSAGE msg;
	struct sockaddr_in* clientSocketStruct = NULL;
	int sockSize = sizeof(struct sockaddr_in);
	SA_PVEHICLE_DATA vehicle;
	while (((msgLength = (int)recv(clientSock, clientBuff, VSC_SOCKBUF_LEN, 0)) > 0) && vehicleServerStat.state == 1) {
		
		printf("Mensaje recibido: %s, msgLength: %d\n", clientBuff, msgLength);
		if (msgLength < VSC_SOCKBUF_LEN && inMsg == 0) {
			if (((stxPos = strchr(clientBuff, '\x02')) != NULL) && ((etxPos  = strchr(clientBuff, '\x03')) != NULL)) {
				msg = calloc(1, sizeof(MESSAGE));
				MP_initMsgStruc(msg, msgLength);
				strcpy(msg->fullMsg, clientBuff);
				msg->msgSize = strlen(msg->fullMsg);
				msg->source = 1;
				memset(clientBuff, 0, VSC_SOCKBUF_LEN);
				msg->isFirstMsg = firstMessage;
				if (firstMessage == 1) {
					msg->isFirstMsg = firstMessage;
					msg->clientSocket = clientSock;
					msg->handlingThread = pthread_self();
					firstMessage = 0;
				}
				MB_putMessage(receivedMsgBuff, msg);
				memset(clientBuff, 0, VSC_SOCKBUF_LEN);
			} else {
				memset(clientBuff, 0, VSC_SOCKBUF_LEN);
			}
		} else {
			if (inMsg == 0 && stxFound == 0) {
				if((stxPos = strchr(clientBuff, '\x02')) != NULL) {	//If STX found, copy socket buffer to inner buffer and start reading
					inMsg = 1;
					msgBuff = calloc(VSC_SOCKBUF_LEN, sizeof(char));
					strcpy(msgBuff, stxPos);
					memset(clientBuff, 0, VSC_SOCKBUF_LEN);
					stxFound = 1;
				} else {
					memset(clientBuff, 0, VSC_SOCKBUF_LEN);				// If not STX found and not in MSG, discard the Junk
				}
			} else if(inMsg == 1 && stxFound == 1) {								//If we are reading a message
				if ((etxPos = strchr(clientBuff, '\x03')) != NULL) {   //If we found ETX, copy contents and end reading.
					realloc(msgBuff, ((strlen(msgBuff)*sizeof(char) + 1) + (etxPos-clientBuff)));
					strncat(msgBuff, clientBuff, (etxPos - clientBuff));
					inMsg = 0;
					stxFound = 0;
					msg = calloc(1, sizeof(MESSAGE));
					MP_initMsgStruc(msg, sizeof(msgBuff));
					strcpy(msg->fullMsg, msgBuff);
					msg->msgSize = strlen(msg->fullMsg);
					msg->source = 1;
					if (firstMessage == 1) {
						msg->isFirstMsg = firstMessage;
						msg->clientSocket = clientSock;
						msg->handlingThread = pthread_self();
						firstMessage = 0;
					}
					MB_putMessage(receivedMsgBuff, msg);
					free(msgBuff);
				} else {															//Else copy all contents and continue reading.
					realloc(msgBuff, ((strlen(msgBuff)*sizeof(char) + 1) + VSC_SOCKBUF_LEN));
					strcat(msgBuff, clientBuff);
				}
			}
		}
	}
	vehicle = SA_searchVehicleByInboxThread(pthread_self());
	if (vehicle != NULL) {
		SA_sendDisconnectedMsg(vehicle->id);
	}
	pthread_exit(NULL);
}

void* VSC_outboundHandlerThreadFunc(void* args) {
	SA_PVEHICLE_DATA vehicle = (SA_PVEHICLE_DATA)args;
	PMESSAGE msg;
	vehicle->outboxThread = pthread_self();
	while (vehicleServerStat.state == 1) {
		msg = MB_getMessage(vehicle->outbox);
		VSC_SendMessageToVehicle(msg, vehicle);
	}
	pthread_exit(NULL);
}

int VSC_SendMessageToVehicle(PMESSAGE msg, SA_PVEHICLE_DATA vehicle) {
	if (send(vehicle->clientSocket, msg->fullMsg, sizeof(msg->fullMsg), NULL) == -1) {
		perror("Error sending message");
		return -1;
	} else {
		MP_wipeMessage(msg);
		return 0;
	}
}
