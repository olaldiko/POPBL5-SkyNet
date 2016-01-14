//
//  stationActions.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 1/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "stationActions.h"

SA_VEHICLE_QUEUE vehicleList;
int stationID = 5;

void SA_treatIDMessage(PMESSAGE msg) {
	
}
void SA_treatIDReqMessage(PMESSAGE msg) {
	sprintf(msg->fullMsg, "\x02%d\x1dIDREQUEST\x1d%d\x03", stationID, msg->clientSocket);
	MB_putMessage(SSC_serverSendBuffer, msg);
}

void SA_treatIDAssign(PMESSAGE msg) {
	SA_PVEHICLE_DATA vehicle;
	if ((vehicle = SA_searchVehicleBySocket(atoi(msg->data))) != NULL) {
		vehicle->id = atoi(msg->id);
		MB_putMessage(vehicle->outbox, msg);
	}
}

void SA_treatLOCMessage(PMESSAGE msg) {
	MB_putMessage(SSC_serverSendBuffer, msg);
}

void SA_treatListMessage(PMESSAGE msg) {
	char* vehicle;
	while((vehicle = strtok(msg->data, "$")) != NULL) {
		SA_addVehicleToList(atoi(vehicle));
	}
}

void SA_treatRouteMessage(PMESSAGE msg) {
	assert(msg->fullMsg != NULL);
	SA_PVEHICLE_DATA vehicle = SA_searchVehicleById(atoi(msg->id));
	PMESSAGE ack;
	if (vehicle == NULL) {
		ack = calloc(1, sizeof(MESSAGE));
		MP_initMsgStruc(ack, 100);
		sprintf(ack->fullMsg, "\x02%s\x1dNACK\x1d%s", msg->id, msg->msgCounter);
		MB_putMessage(SSC_serverSendBuffer, ack);
	} else {
		MB_putMessage(vehicle->outbox, msg);
	}
}

void SA_initVehicle(SA_PVEHICLE_DATA vehicle, int id) {
	vehicle->id = id;
	vehicle->outbox = MB_initBuffer(10);
	pthread_create(&vehicle->outboxThread, NULL, VSC_outboundHandlerThreadFunc, vehicle);
}

void SA_treatStatusMessage(PMESSAGE msg) {
	MB_putMessage(SSC_serverSendBuffer, msg);
}

void SA_treatAlertMessage(PMESSAGE msg) {
	MB_putMessage(mcmServerStats.stationOutbox, msg);
}



void SA_initVehicleList() {
	pthread_mutex_init(&vehicleList.mtx, NULL);
	vehicleList.numElems = 0;
	vehicleList.head = NULL;
}

SA_PVEHICLE_DATA SA_addVehicleToList(int id) {
	pthread_mutex_lock(&vehicleList.mtx);
	SA_PVEHICLE_ELEM element = calloc(1, sizeof(SA_VEHICLE_ELEM));
	SA_initVehicle(&element->vehicle, id);
	SA_PVEHICLE_ELEM queueCursor;
	element->vehicle.id = id;
	if(vehicleList.head == NULL) {
		vehicleList.head = element;
	} else {
		for (queueCursor = vehicleList.head; queueCursor->next != NULL; queueCursor = queueCursor->next);
		queueCursor->next = element;
	}
	element->vehicle.outbox = MB_initBuffer(10);
	pthread_mutex_unlock(&vehicleList.mtx);
	return &element->vehicle;
}

SA_PVEHICLE_DATA SA_searchVehicleBySocket(int socket) {
	SA_PVEHICLE_ELEM queueCursor;
	SA_PVEHICLE_DATA retVal = NULL;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		retVal = NULL;
	} else {
		for (queueCursor = vehicleList.head; ((queueCursor->next != NULL) && (queueCursor->vehicle.clientSocket != socket)); queueCursor = queueCursor->next);
		if (queueCursor->vehicle.clientSocket == socket) {
			retVal = &queueCursor->vehicle;
		} else {
			retVal = NULL;
		}
	}
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}

SA_PVEHICLE_DATA SA_searchVehicleById(int id) {
	SA_PVEHICLE_ELEM queueCursor;
	SA_PVEHICLE_DATA retVal = NULL;
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

SA_PVEHICLE_DATA SA_searchVehicleByInboxThread(pthread_t inboxThread) {
	SA_PVEHICLE_ELEM queueCursor;
	SA_PVEHICLE_DATA retVal;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		retVal = NULL;
	} else {
		for (queueCursor = vehicleList.head; ((queueCursor->next != NULL) && (queueCursor->vehicle.inboxThread = inboxThread)); queueCursor = queueCursor->next);
		if (queueCursor->vehicle.inboxThread == inboxThread) {
			retVal = &queueCursor->vehicle;
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
	pthread_mutex_lock(&vehicleList.mtx);
	for (queueCursor = vehicleList.head, i = 0; i < index && queueCursor != NULL; queueCursor = queueCursor->next, i++);
	pthread_mutex_unlock(&vehicleList.mtx);
	return &queueCursor->vehicle;
}
void SA_sendConnectedMsg(int id) {
	PMESSAGE msg = calloc(1, sizeof(MESSAGE));
	MP_initMsgStruc(msg, 100);
	sprintf(msg->fullMsg, "\x02%d%sCONNECTED\x1d%d\x03", id, "\x1d",stationID);
	MB_putMessage(SSC_serverSendBuffer, msg);
}

void SA_sendDisconnectedMsg(int id) {
	PMESSAGE msg = calloc(1, sizeof(MESSAGE));
	MP_initMsgStruc(msg, 100);
	sprintf(msg->fullMsg, "\x02%d%sDISCONNECTED\x1d%d\x03", id, "\x1d",stationID);
	MB_putMessage(SSC_serverSendBuffer, msg);
}

void SA_sendJoinMessage(int id) {
	SA_PVEHICLE_DATA vehicle = SA_searchVehicleById(id);
	PMESSAGE msg = calloc(1, sizeof(MESSAGE));
	MP_initMsgStruc(msg, 100);
	if (vehicle != NULL) {
		sprintf(msg->fullMsg, "\x02%d\x1dJOIN\x1d%s\x03", stationID, inet_ntoa(mcmServerStats.stationGroup));
		MB_putMessage(vehicle->outbox, msg);
	}
}

