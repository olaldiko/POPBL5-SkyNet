//
//  structures.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 5/1/16.
//  Copyright © 2016 Gorka Olalde Mendia. All rights reserved.
//

#ifndef structures_h
#define structures_h
#include <stdio.h>
#include <pthread.h>
#include <netinet/in.h>

//messageparser.h

typedef struct MESSAGE{
	int source; //0-Server, 1-Vehicle
	in_addr_t srcAddress;
	int clientSocket;
	struct sockaddr_in* clientSocketStruct;
	pthread_t handlingThread;
	int isFirstMsg;
	int msgSize;
	char* fullMsg;
	char* id;
	char* dataType;
	char* data;
	char* msgCounter;
}MESSAGE, *PMESSAGE;


//msgBuffers.h

typedef struct MSGQUEUE {
	struct MESSAGE *msg;
	struct MSGQUEUE *next;
}MSGQUEUE, *PMSGQUEUE;

typedef struct MSGBUFF {
	pthread_mutex_t mtx;
	pthread_cond_t empty;
	pthread_cond_t full;
	int cant;
	int maxVals;
	PMSGQUEUE head;
}MSGBUFF, *PMSGBUFF;


//stationActions.h

typedef struct SA_VEHICLE_DATA {
	int id;
	int clientSocket;
	struct sockaddr_in clientSocketStruct;
	PMSGBUFF outbox;
	pthread_t inboxThread;
	pthread_t outboxThread;
}SA_VEHICLE_DATA, *SA_PVEHICLE_DATA;

typedef struct SA_VEHICLE_ELEM {
	SA_VEHICLE_DATA vehicle;
	struct SA_VEHICLE_ELEM* next;
}SA_VEHICLE_ELEM, *SA_PVEHICLE_ELEM;

typedef struct SA_VEHICLE_QUEUE {
	pthread_mutex_t mtx;
	SA_PVEHICLE_ELEM head;
	int numElems;
}SA_VEHICLE_QUEUE, *SA_PVEHICLE_QUEUE;


//tcpServerSocketComm.h
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

//tcpVehicleSocketComm.h

typedef struct VSC_STAT {
	int state;
	int serverSocket;
	struct sockaddr_in serverSocketStruct;
	struct hostent *serverInfo;
	int sockSize;
	char* buffer;
	pthread_t listenThread;
}VSC_STAT, *VSC_PSTAT;


#endif /* structures_h */
