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
#include "structures.h"
#include "messageParser.h"
#include "msgBuffers.h"
#include "tcpServerSocketComm.h"
#include "tcpVehicleSocketComm.h"
#include "mcastMessaging.h"

extern SA_VEHICLE_QUEUE vehicleList;
extern int stationID;

void SA_treatIDMessage(PMESSAGE msg);
void SA_treatIDReqMessage(PMESSAGE msg);
void SA_treatIDAssign(PMESSAGE msg);
void SA_treatLOCMessage(PMESSAGE msg);
void SA_treatRouteMessage(PMESSAGE msg);
void SA_treatListMessage(PMESSAGE msg);
void SA_treatStatusMessage(PMESSAGE msg);
void SA_treatAlertMessage(PMESSAGE msg);
void SA_sendConnectedMsg(int id);
void SA_sendDisconnectedMsg(int id);
void SA_sendJoinMessage(int id);
void SA_initVehicleList();
void SA_initVehicle(SA_PVEHICLE_DATA vehicle, int id);
SA_PVEHICLE_DATA SA_addVehicleToList(int id);
SA_PVEHICLE_DATA SA_searchVehicleById(int id);
SA_PVEHICLE_DATA SA_searchVehicleBySocket(int socket);
SA_PVEHICLE_DATA SA_searchVehicleByInboxThread(pthread_t inboxThread);
int SA_countVehiclesInList();
SA_PVEHICLE_DATA SA_getVehicleByIndex(int index);
#endif /* stationActions_h */
