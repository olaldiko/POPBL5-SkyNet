//
//  stationActions.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 1/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#ifndef stationActions_h
#define stationActions_h

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include "messageParser.h"
#include "msgBuffers.h"
#include "tcpServerSocketComm.h"
#include "tcpVehicleSocketComm.h"

typedef struct SA_VEHICLE_DATA {
	int id;
	struct in_addr ipAddress;
	int clientSocket;
	struct sockaddr_in clientSocketStruct;
	pthread_t clientThread;
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

void SA_treatIDMessage(PMESSAGE msg);
void SA_treadIDReqMessage(PMESSAGE msg);
void SA_treadLOCMessage(PMESSAGE msg);

SA_VEHICLE_QUEUE vehicleList;

void SA_initVehicleList();
SA_PVEHICLE_DATA SA_addVehicleToList(int id);
SA_PVEHICLE_DATA SA_searchVehicleById(int id);
SA_PVEHICLE_DATA SA_searchVehicleByAddress(struct in_addr *address);

#endif /* stationActions_h */
