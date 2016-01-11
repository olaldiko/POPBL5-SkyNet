//
//  messageParser.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 30/11/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "messageParser.h"

PMSGBUFF receivedMsgBuff;
int parserState;

void MP_initMsgStruc(PMESSAGE msg, int msgSize) {
    msg->source				= 0;
	msg->msgSize			= msgSize;
    msg->fullMsg			= calloc(msgSize, sizeof(char));
    msg->id					= calloc(MSG_IDSIZE, sizeof(char));
    msg->dataType			= calloc(MSG_TYPESIZE, sizeof(char));
    msg->data				= calloc(msgSize, sizeof(char));
	msg->msgCounter			= 0;
	msg->clientSocket		= 0;
}

int MP_parseMessage(PMESSAGE msg) {
    char *strippedMsg = calloc(msg->msgSize, sizeof(char));
	SA_PVEHICLE_DATA vehicle;
    if((strcspn(msg->fullMsg, "\x02") == 0) && (strcspn(msg->fullMsg, "\x03") == strlen(msg->fullMsg)-1)) {
		strcpy(strippedMsg, msg->fullMsg +1);
		strippedMsg[strlen(strippedMsg)-1] = '\x00';
		strcpy(msg->id, strtok(strippedMsg, "\x1d\x02\x03"));
		strcpy(msg->dataType, strtok(NULL, "\x1d\x02\x03"));
		strcpy(msg->data, strtok(NULL, "\x1d\x02\x03"));
        if (msg->source == 0) {
			if(strcmp(msg->dataType, "ROUTE") == 0)				SA_treatRouteMessage(msg);
			else if (strcmp(msg->dataType, "ALERT") == 0)		SA_treatAlertMessage(msg);
			else if	(strcmp(msg->dataType, "IDASSIGN") == 0) 	SA_treatIDResponse(msg);
			else if	(strcmp(msg->dataType, "RESOURCES") == 0)	SA_treatListMessage(msg);
        } else {
			if (msg->isFirstMsg) {
				if((vehicle = SA_searchVehicleById(atoi(msg->id))) != NULL) {
					close(vehicle->clientSocket);
					vehicle->clientSocket = msg->clientSocket;
					pthread_join(vehicle->inboxThread, NULL);
					vehicle->inboxThread = msg->handlingThread; //If we are receiving a message from a new thread, the old one shoud have ended when calling the close function.
				} else {
					vehicle = SA_addVehicleToList(atoi(msg->id));
					vehicle->clientSocket = msg->clientSocket;
					vehicle->inboxThread = msg->handlingThread;
				}
				SA_sendConnectedMsg(atoi(msg->id));
			}
			if(strcmp(msg->dataType, "ID") == 0)				SA_treatIDMessage(msg);
			else if (strcmp(msg->dataType, "IDREQUEST") == 0)   SA_treatIDReqMessage(msg);
			else if (strcmp(msg->dataType, "LOCATION") == 0)    SA_treatLOCMessage(msg);
			else if (strcmp(msg->dataType, "STATUS") == 0)		SA_treatStatusMessage(msg);
        }
		free(strippedMsg);
        return 0;
    } else {
		free(strippedMsg);
        return -1;
    }
}

void MP_initParser() {
	parserState = 1;
	pthread_t parserThread;
	receivedMsgBuff = MB_initBuffer(20);
	pthread_create(&parserThread, NULL, MP_ParserThread, NULL);

}

void MP_shutdownParser() {
	parserState = 0;
	
}

void* MP_ParserThread(void* args) {
	while (parserState) {
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
