//
//  messageParser.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 30/11/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "messageParser.h"

PMSGBUFF receivedMsgBuff;

void MP_initMsgStruc(PMESSAGE msg, int msgSize) {
    msg->source			= 0;
    msg->msgSize		= msgSize;
    msg->fullMsg		= calloc(msgSize, sizeof(char));
    msg->id				= calloc(MSG_IDSIZE, sizeof(char));
    msg->dataType       = calloc(MSG_TYPESIZE, sizeof(char));
    msg->data			= calloc(msgSize-MSG_IDSIZE-MSG_TYPESIZE, sizeof(char));
	msg->msgCounter		= 0;
}

int MP_parseMessage(PMESSAGE msg) {
    char *strippedMsg = calloc(msg->msgSize, sizeof(char));
	SA_PVEHICLE_DATA vehicle;
    if((strcspn(msg->fullMsg, "\x02") == 0) && (strcspn(msg->fullMsg, "\x03") == strlen(msg->fullMsg))) {
		strcpy(strippedMsg, msg->fullMsg +1);
		strippedMsg[strlen(strippedMsg)-1] = '\x00';
		strcpy(msg->id, strtok(strippedMsg, "\x1d\x02\x03"));
		strcpy(msg->dataType, strtok(NULL, "\x1d\x02\x03"));
		strcpy(msg->data, strtok(NULL, "\x1d\x02\x03"));
		strcpy(msg->msgCounter, strtok(NULL, "\x1d\x02\x03"));
        if (msg->source == 0) {
			if(strcmp(msg->dataType, "RUT"))         {}   //MP_parseRouteMessage(msg);
			else if (strcmp(msg->dataType, "ALT"))   {}   //MP_parseServerAlert(msg);
			else if	(strcmp(msg->dataType, "IDANS")) {}	//SA_treadIDResponse(msg);
        } else {
			if (msg->isFirstMsg) {
				if((vehicle = SA_searchVehicleById(atoi(msg->id))) != NULL) {
					close(vehicle->clientSocket);
					vehicle->clientSocket = msg->clientSocket;
					vehicle->clientSocketStruct = *msg->clientSocketStruct;
					pthread_join(vehicle->inboxThread, NULL);
					vehicle->inboxThread = msg->handlingThread; //If we are receiving a message from a new thread, the old one shoud have ended when calling the close function.
				} else {
					vehicle = SA_addVehicleToList(atoi(msg->id));
					vehicle->clientSocket = msg->clientSocket;
					vehicle->clientSocketStruct = *msg->clientSocketStruct;
					vehicle->inboxThread = msg->handlingThread;
				}
			}
			if(strcmp(msg->dataType, "ID"))             SA_treatIDMessage(msg);
			else if (strcmp(msg->dataType, "IDREQ"))    SA_treatIDReqMessage(msg);
			else if (strcmp(msg->dataType, "LOC"))      SA_treatLOCMessage(msg);
			//else if (strcmp(msg->dataType, "STAT"))
           // else if (strcmp(msg->dataType, "ACK"))      MP_parseVehicleACK(msg);
           // else if (strcmp(msg->dataType, "NACK"))     MP_parseVehicleNACK(msg);
        }
        return 0;
    } else {
        return -1;
    }
}

void* MP_ParserThread(void* args) {
	int state = 1;
	while (state) {
	PMESSAGE msg = MB_getMessage(receivedMsgBuff);
	MP_parseMessage(msg);
	}
	pthread_exit(NULL);
	return 0;
}

void MP_createACK(int msgCounter) {
	PMESSAGE msg = calloc(1, sizeof(MESSAGE));
	MP_initMsgStruc(msg, 200);
	//To-Do: Generate ACKs
}

void MP_wipeMessage(PMESSAGE msg) {
	free(msg->fullMsg);
	free(msg->data);
	free(msg->dataType);
	free(msg->id);
	free(msg);
}
