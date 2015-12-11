//
//  socketComm.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 1/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//
/*
#include "vehicleSocketComm.h"





int VSC_serverStartup(int port) {
    vehicleServerStat.sockSize = sizeof(struct sockaddr_in);
    vehicleServerStat.serverSocketStruct.sin_family = AF_INET;
    vehicleServerStat.serverSocketStruct.sin_port = htons(port);
    vehicleServerStat.serverSocketStruct.sin_addr.s_addr = htonl(INADDR_ANY);
    vehicleServerStat.serverBuffer = calloc(SRV_MAXBUFF, sizeof(char));
    
    if ((vehicleServerStat.serverSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1) return -1;
    if (bind(vehicleServerStat.serverSocket, (struct sockaddr*)&vehicleServerStat.serverSocketStruct, vehicleServerStat.sockSize) == -1) return -1;
	receivedMsgBuff = MB_initBuffer(50);
	assert(receivedMsgBuff == NULL);
	pthread_create(&listenThread, NULL, VSC_receiverThreadFunc, NULL);
    return 0;
}


void* VSC_receiverThreadFunc(void* args) {
	PMESSAGE msg;
	int state = 1;
	int recv_length = 0;
	struct sockaddr_in clientSocket;
	while (state) {
		if((recv_length = recvfrom(vehicleServerStat.serverSocket, vehicleServerStat.serverBuffer, SRV_MAXBUFF, 0, (struct sockaddr*)&clientSocket, &vehicleServerStat.sockSize)) == -1) {
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


int VSC_sendMessage(PMESSAGE msg) {
	sendto(vehicleServerStat.serverSocket, msg->fullMsg, msg->msgSize, 0, (struct sockaddr*)&msg->clientSocket, sizeof(struct sockaddr_in));
    if(msg->msgType == 2) {
        if (msg->waitACK) {
            
            //TO-DO: ACK managing
            
        }
    }
	return 0;
}
*/
