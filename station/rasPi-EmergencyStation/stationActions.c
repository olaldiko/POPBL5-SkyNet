//
//  stationActions.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 1/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "stationActions.h"


void SA_treatIDMessage(PMESSAGE msg) {
	MB_putMessage(&SSC_serverSendBuffer, msg);
}

void SA_treadIDReqMessage(PMESSAGE msg) {
	
}

void SA_treadLOCMessage(PMESSAGE msg) {
	MB_putMessage(&SSC_serverSendBuffer, msg);
	
}

void SA_treatListMessage(PMESSAGE msg) {
	char* vehicle;
	struct in_addr address;
	vehicle = strtok(msg->data, "$");
	SA_addVehicleToList(atoi(vehicle));
}

void SA_initVehicleList() {
	pthread_mutex_init(&vehicleList.mtx, NULL);
	vehicleList.numElems = 0;
	vehicleList.head = NULL;
}

void SA_addVehicleToList(int id) {
	pthread_mutex_lock(&vehicleList.mtx);
	SA_PVEHICLE_ELEM element = calloc(1, sizeof(SA_VEHICLE_ELEM));
	SA_PVEHICLE_ELEM queueCursor;
	element->vehicle.id = id;
	if(vehicleList.head == NULL) {
		vehicleList.head = element;
	} else {
		for (queueCursor = vehicleList.head; queueCursor->next != NULL; queueCursor = queueCursor->next);
		queueCursor->next = element;
	}
	pthread_mutex_unlock(&vehicleList.mtx);
}

SA_PVEHICLE_DATA SA_searchVehicle(int id, struct in_addr address) {
	SA_PVEHICLE_ELEM queueCursor;
	SA_PVEHICLE_DATA retVal;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		retVal = NULL;
	} else {
		if (id > 0) { //We search by ID
			for (queueCursor = vehicleList.head; ((queueCursor->next != NULL) && (queueCursor->vehicle.id != id)); queueCursor = queueCursor->next);
			 if (queueCursor->vehicle.id == id) {
				 retVal = &queueCursor->vehicle;
			 } else {
				 retVal = NULL;
			 }
		} else { //We search by Address
			for (queueCursor = vehicleList.head; ((queueCursor->next != NULL) && (queueCursor->vehicle.ipAddress.s_addr != address.s_addr)); queueCursor = queueCursor->next);
			 if (queueCursor->vehicle.ipAddress.s_addr == address.s_addr) {
				 retVal =  &queueCursor->vehicle;
			 } else {
				 retVal = NULL;
			 }
		}
	}
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}