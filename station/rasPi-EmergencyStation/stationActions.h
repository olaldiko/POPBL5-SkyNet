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
#include <pthread.h>
#include "messageParser.h"
#include "msgBuffers.h"
#include "serverSocketComm.h"
#include "vehicleSocketComm.h"

typedef struct SA_VEHICLE_DATA {
	int id;
	struct in_addr ipAddress;
	struct sockaddr_in clientSocket;
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
void SA_addVehicleToList(int id);
SA_PVEHICLE_DATA SA_searchVehicle(int id, struct in_addr address);

#endif /* stationActions_h */
