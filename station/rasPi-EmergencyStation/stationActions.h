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



void SA_treatIDMessage(PMESSAGE msg);
void SA_treatIDReqMessage(PMESSAGE msg);
void SA_treatLOCMessage(PMESSAGE msg);

extern SA_VEHICLE_QUEUE vehicleList;

void SA_initVehicleList();
void SA_initVehicle(SA_PVEHICLE_DATA vehicle, int id);
SA_PVEHICLE_DATA SA_addVehicleToList(int id);
SA_PVEHICLE_DATA SA_searchVehicleById(int id);
int SA_countVehiclesInList();
SA_PVEHICLE_DATA SA_getVehicleByIndex(int index);
#endif /* stationActions_h */
