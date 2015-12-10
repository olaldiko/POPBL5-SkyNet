//
//  tcpServerSocketComm.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 9/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "tcpServerSocketComm.h"


void SSC_initServerConnection() {
	int waitTime = 1;
	serverSocketStat.buffer = calloc(SSC_SRV_BUFLEN, sizeof(char));
	serverSocketStat.sockSize = sizeof(struct sockaddr_in);
	serverSocketStat.serverInfo = NULL;
	serverSocketStat.serverSocket = socket(AF_INET, SOCK_STREAM, 0);
	while (!serverSocketStat.serverInfo) {
		printf("Trying to search server IP address....\n");
		serverSocketStat.serverInfo = gethostbyname(SSC_SRV_ADDRESS);
		if (serverSocketStat.serverInfo == NULL) {
			printf("Host unreachable\n");
			sleep(waitTime);
			waitTime <<= 1;
		}
	}
	printf("Server IP address: %s\n", serverSocketStat.serverInfo->h_addr_list[0]);
	serverSocketStat.serverSocketStruct.sin_family = AF_INET;
	serverSocketStat.serverSocketStruct.sin_len = sizeof(struct sockaddr_in);
	inet_aton(serverSocketStat.serverInfo->h_addr_list[0], (struct in_addr *)&serverSocketStat.serverSocketStruct.sin_addr.s_addr);
	serverSocketStat.serverSocketStruct.sin_port = htons(SSC_SRV_PORT);
	SSC_makeServerConnection();
	
}

void SSC_initServerConnThreads() {
	pthread_create(&serverSocketStat.listenThread, NULL, msgListenerThreadFunc, NULL);
	pthread_create(&serverSocketStat.sendThread, NULL, msgSenderThreadFunc, NULL);
}

void SSC_stopServerConnThreads() {
	serverSocketStat.state = 0;
	pthread_cancel(serverSocketStat.listenThread);
	pthread_cancel(serverSocketStat.sendThread);
	//TO-DO: Improve thread termination
}

void SSC_makeServerConnection() {
	int waitTime = 1;
	close(serverSocketStat.serverSocket);
	while (connect(serverSocketStat.serverSocket, (struct sockaddr *)&serverSocketStat.serverSocketStruct, serverSocketStat.sockSize) == -1) {
		printf("Trying to connect to server...\n");
		sleep(waitTime);
		waitTime <<= 1;
		close(serverSocketStat.serverSocket); //Advanced Programming in the Unix Enviorement, page 607,
											  //for testing in OSX, as after failed conexions, in *nix the socket is in an undefined state.
	}
}

void SSC_sendMessageToServer(PMESSAGE msg) {
	send(serverSocketStat.serverSocket, msg->fullMsg, strlen(msg->fullMsg), 0);
}

void SSC_listenToServerMsg() {
	int msgLength = 0;
	int inMsg = 0;
	int stxFound = 0;
	char* stxPos;
	char* etxPos;
	char* msgBuff = NULL;
	PMESSAGE msg;
	while (((msgLength = recv(serverSocketStat.serverSocket, serverSocketStat.buffer,SSC_SRV_BUFLEN , 0)) > 0) && serverSocketStat.state == 1) {
		if (msgLength < SSC_SRV_BUFLEN && inMsg == 0) {
			msg = calloc(1, sizeof(MESSAGE));
			MP_initMsgStruc(msg, msgLength);
			strcpy(msg->fullMsg, serverSocketStat.buffer);
			memset(serverSocketStat.buffer, 0, SSC_SRV_BUFLEN);
			MB_putMessage(receivedMsgBuff, msg);
			memset(serverSocketStat.buffer, 0, SSC_SRV_BUFLEN);
		} else {
			if (inMsg == 0 && stxFound == 0) {
				if((stxPos = strchr(serverSocketStat.buffer, '\x02')) != NULL) {	//If STX found, copy socket buffer to inner buffer and start reading
					inMsg = 1;
					msgBuff = calloc(SSC_SRV_BUFLEN, sizeof(char));
					strcpy(msgBuff, stxPos+1);
					memset(serverSocketStat.buffer, 0, SSC_SRV_BUFLEN);
					stxFound = 1;
				} else {
					memset(serverSocketStat.buffer, 0, SSC_SRV_BUFLEN);				// If not STX found and not in MSG, discard the Junk
				}
			} else if(inMsg == 1 && stxFound == 1) {								//If we are reading a message
				if ((etxPos = strchr(serverSocketStat.buffer, '\x03')) != NULL) {   //If we found ETX, copy contents and end reading.
					realloc(msgBuff, ((strlen(msgBuff)*sizeof(char) + 1) + (etxPos-serverSocketStat.buffer)));
					strncat(msgBuff, serverSocketStat.buffer, ((etxPos-1) - serverSocketStat.buffer));
					inMsg = 0;
					stxFound = 0;
					msg = calloc(1, sizeof(MESSAGE));
					MP_initMsgStruc(msg, sizeof(msgBuff));
					strcpy(msg->fullMsg, msgBuff);
					free(msgBuff);
				} else {															//Else copy all contents and continue reading.
					realloc(msgBuff, ((strlen(msgBuff)*sizeof(char) + 1) + SSC_SRV_BUFLEN));
					strcat(msgBuff, serverSocketStat.buffer);
				}
			}
		}
	}
}

void* msgSenderThreadFunc(void* args) {
	PMESSAGE msg;
	while (serverSocketStat.state) {
		msg = MB_getMessage(&SSC_serverSendBuffer);
		SSC_sendMessageToServer(msg);
		printf("%s sent to server", msg->fullMsg);
		MP_wipeMessage(msg);
	}
	pthread_exit(NULL);
}

void* msgListenerThreadFunc(void* args) {
	while (serverSocketStat.state) {
		SSC_listenToServerMsg();
		if (serverSocketStat.state == 1) {
			SSC_makeServerConnection();
		}
		
	}
	pthread_exit(NULL);
}