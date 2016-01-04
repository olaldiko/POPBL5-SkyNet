//
//  stationActions.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 1/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "stationActions.h"



void SA_treadIDReqMessage(PMESSAGE msg) {
	SSC_sendMessageToServer(msg);
}

//TO-DO: Create a separate queue for ID requests, manage to assign the IDs in the correct order
void SA_treadIDResponse(PMESSAGE msg) {
	SA_PVEHICLE_DATA vehicle;
	if ((vehicle = SA_searchVehicleById(-1)) != NULL) {
		vehicle->id = atoi(msg->data);
	}
	
}

void SA_treadLOCMessage(PMESSAGE msg) {
	SSC_sendMessageToServer(msg);
}

void SA_treatListMessage(PMESSAGE msg) {
	char* vehicle;
	while((vehicle = strtok(msg->data, "$")) != NULL) {
		SA_addVehicleToList(atoi(vehicle));
	}
}

void SA_treatRouteMessage(PMESSAGE msg) {
	SA_PVEHICLE_DATA vehicle = SA_searchVehicleById(atoi(msg->id));
	PMESSAGE ack;
	if (vehicle == NULL || VSC_SendMessageToVehicle(msg, vehicle) == -1) {
		ack = calloc(1, sizeof(MESSAGE));
		MP_initMsgStruc(ack, 100);
		sprintf(msg->fullMsg, "\x02%s\x1dNACK\x1d%s", msg->id, msg->msgCounter);
		MB_putMessage(&SSC_serverSendBuffer, msg);
	} else {
		//Send ACK also?
	}
}

void SA_treatStatMessage(PMESSAGE msg) {
	SSC_sendMessageToServer(msg);
}

void SA_treatAlertMessage(PMESSAGE msg) {
	//TO-DO: Pending to do it via MCAST or unicast.
}

void SA_initVehicleList() {
	pthread_mutex_init(&vehicleList.mtx, NULL);
	vehicleList.numElems = 0;
	vehicleList.head = NULL;
}

SA_PVEHICLE_DATA SA_addVehicleToList(int id) {
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
	return &element->vehicle;
}

SA_PVEHICLE_DATA SA_searchVehicleById(int id) {
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
		}
	}
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}

SA_PVEHICLE_DATA SA_searchVehicleByAddress(struct in_addr *address) {
	SA_PVEHICLE_ELEM queueCursor;
	SA_PVEHICLE_DATA retVal = NULL;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		retVal = NULL;
	} else { //We search by Address
		for (queueCursor = vehicleList.head; ((queueCursor->next != NULL) && (queueCursor->vehicle.ipAddress.s_addr != address->s_addr)); queueCursor = queueCursor->next);
		if (queueCursor->vehicle.ipAddress.s_addr == address->s_addr) {
			retVal =  &queueCursor->vehicle;
		} else {
			retVal = NULL;
		}
	}
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}

int SA_countVehiclesInList() {
	int retVal = 0;
	SA_PVEHICLE_ELEM queueCursor;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		return 0;
	}
	for (queueCursor = vehicleList.head; queueCursor != NULL; queueCursor = queueCursor->next, retVal++);
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}
SA_PVEHICLE_DATA SA_getVehicleByIndex(int index) {
	int i = 0;
	SA_PVEHICLE_ELEM queueCursor;
	for (queueCursor = vehicleList.head, i = 0; i < index && queueCursor != NULL; queueCursor = queueCursor->next, i++);
	return &queueCursor->vehicle;
}