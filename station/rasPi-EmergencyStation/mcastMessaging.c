//
//  mcastMessaging.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 12/1/16.
//  Copyright © 2016 Gorka Olalde Mendia. All rights reserved.
//

#include "mcastMessaging.h"

MCM_serverStats mcmServerStats;
void MCM_initMcastServer() {
	mcmServerStats.generalInbox = MB_initBuffer(10);
	mcmServerStats.stationOutbox = MB_initBuffer(10);
	MCM_initSockets();
	MCM_initGeneralGroup();
	MCM_initStationGroup();
	
}

void MCM_initSockets() {
	int yes = 1;
	if ((mcmServerStats.generalSocket = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		perror("Error creating general MCAST socket");
	}
	if ((mcmServerStats.stationSocket  = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		perror("Error creating station MCAST socket");
	}
	
	if (setsockopt(mcmServerStats.generalSocket, SOL_SOCKET, SO_REUSEPORT, &yes, sizeof(int)) < 0) {
		perror("Error seting MCAST sock options");
	}
	if (setsockopt(mcmServerStats.stationSocket, SOL_SOCKET, SO_REUSEPORT, &yes, sizeof(int)) < 0) {
		perror("Error seting MCAST sock options");
	}
}

struct in_addr MCM_calcStationAddress() {
	struct in_addr address;
	inet_aton(MCM_GENERAL_GRP, &address);
	
	address.s_addr = ((address.s_addr>>24)&0xff) | ((address.s_addr<<8)&0xff0000) | ((address.s_addr>>8)&0xff00) | ((address.s_addr<<24)&0xff000000);
	address.s_addr += stationID;
	address.s_addr = ((address.s_addr>>24)&0xff) | ((address.s_addr<<8)&0xff0000) | ((address.s_addr>>8)&0xff00) | ((address.s_addr<<24)&0xff000000);
	return address;
}

void MCM_initStationGroup() {
	mcmServerStats.stationGroup = MCM_calcStationAddress();
	
}

void MCM_initGeneralGroup() {
	mcmServerStats.generalMreq.imr_multiaddr.s_addr = inet_addr(MCM_GENERAL_GRP);
	mcmServerStats.generalMreq.imr_interface.s_addr = inet_addr(INADDR_ANY);
	if ((setsockopt(mcmServerStats.generalSocket, IPPROTO_IP, IP_ADD_MEMBERSHIP, &mcmServerStats.generalMreq, sizeof(struct ip_mreq))) < 0) {
		perror("Error joining MCAST group");
	}
	
}

void* MCM_listenerThread(void* args) {
	struct sockaddr_in address;
	int structSize = sizeof(struct sockaddr_in);
	MCM_threadStruct* opts = (MCM_threadStruct *)args;
	int socket = (int)args;
	struct in_addr addr;
	PMESSAGE msg;
	char* buffer = calloc(MCM_BUFFSIZE, sizeof(char));
	MP_PRECEIVERSTR receiver = calloc(1, sizeof(MP_RECEIVERSTR));
	receiver->clientBuff = buffer;
	while ((receiver->msgLength = recvfrom(opts->socket, buffer, MCM_BUFFSIZE, 0, (struct sockaddr *)&address, &structSize)) && mcmServerStats.state == 1) {
		msg = MP_messageReceiver(receiver);
		msg->source = 0;
		if (msg != NULL) {
			MB_putMessage(mcmServerStats.generalInbox, msg);
		}
	}
	
	pthread_exit(NULL);
}

void* MCM_senderThread(void* args) {
	MCM_threadStruct* opts = (MCM_threadStruct *)args;
	PMESSAGE msg;
	int structSize = sizeof(struct sockaddr_in);
	while (mcmServerStats.state == 1) {
		msg = MB_getMessage(opts->buffer);
		if ((sendto(opts->socket, msg->fullMsg, msg->msgSize, 0, (struct sockaddr *)&opts->address, &structSize)) < 0) {
			perror("Error sending via MCAST");
		}
	}
	pthread_exit(NULL);
}