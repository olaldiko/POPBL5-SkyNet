/** @file messageParser.c Message parser functions definition. */

#include "messageParser.h"

PMSGBUFF receivedMsgBuff;
int parserState;
/**
 * Message structure initializer. Initializes the values of a message structure and allocates the memory.
 * @param msg The message structure.
 * @param msgSize The size of the message.
 **/
void MP_initMsgStruc(PMESSAGE msg, int msgSize) {
    msg->source				= 0;
	msg->msgSize			= msgSize;
    msg->fullMsg			= calloc(msgSize+1, sizeof(char));
    msg->id					= calloc(MSG_IDSIZE, sizeof(char));
    msg->dataType			= calloc(MSG_TYPESIZE, sizeof(char));
    msg->data				= calloc(msgSize+1, sizeof(char));
	msg->msgCounter			= 0;
	msg->clientSocket		= 0;
}
/**
 * Message parser main function. Receives a message structure as parameter and decides wich action to take based on the message type.
 * @param msg The message structure
 * @return If the message is valid, returns 0. Else -1.
 **/
int MP_parseMessage(PMESSAGE msg) {
    char *strippedMsg = calloc(msg->msgSize+1, sizeof(char));
	SA_PVEHICLE_DATA vehicle;
    if((strcspn(msg->fullMsg, "\x02") == 0) && (strcspn(msg->fullMsg, "\x03") == strlen(msg->fullMsg)-1)) {
		strcpy(strippedMsg, msg->fullMsg);
		strcpy(msg->id, strtok(strippedMsg, "\x1d\x02\x03"));
		strcpy(msg->dataType, strtok(NULL, "\x1d\x02\x03"));
		strcpy(msg->data, strtok(NULL, "\x1d\x02\x03"));
        if (msg->source == 0) {
			if(strcmp(msg->dataType, "ROUTE") == 0)				SA_treatRouteMessage(msg);
			else if (strcmp(msg->dataType, "ALERT") == 0)		SA_treatAlertMessage(msg);
			else if	(strcmp(msg->dataType, "IDASSIGN") == 0) 	SA_treatIDAssign(msg);
			else if	(strcmp(msg->dataType, "RESOURCES") == 0)	SA_treatListMessage(msg);
        } else {
			if (msg->isFirstMsg) {
				if((vehicle = SA_searchVehicleById(atoi(msg->id))) != NULL) {
					close(vehicle->clientSocket);
					vehicle->clientSocket = msg->clientSocket;
					pthread_join(vehicle->inboxThread, NULL);
					vehicle->inboxThread = msg->handlingThread;
					vehicle->isConnected = 1;
				} else {
					vehicle = SA_addVehicleToList(atoi(msg->id));
					vehicle->clientSocket = msg->clientSocket;
					vehicle->inboxThread = msg->handlingThread;
					vehicle->isConnected = 1;
				}
				SA_sendConnectedMsg(atoi(msg->id));
				SA_sendJoinMessage(atoi(msg->id));
			}
			if(strcmp(msg->dataType, "ID") == 0)				SA_treatIDMessage(msg);
			else if (strcmp(msg->dataType, "IDREQUEST") == 0)   SA_treatIDReqMessage(msg);
			else if (strcmp(msg->dataType, "LOCATION") == 0)    SA_treatLOCMessage(msg);
			else if (strcmp(msg->dataType, "ESTADO") == 0)		SA_treatStatusMessage(msg);
        }
		free(strippedMsg);
        return 0;
    } else {
		free(strippedMsg);
        return -1;
    }
}
/**
 * Parser thread initializer. Starts the thread that will read the values from the mailbox and treats them.
 *
 **/
void MP_initParser() {
	parserState = 1;
	pthread_t parserThread;
	receivedMsgBuff = MB_initBuffer(20);
	pthread_create(&parserThread, NULL, MP_ParserThread, NULL);

}
/**
 * Parser thread shutdown. Sets the exit condition used by the parser thread to shutdown it.
 **/
void MP_shutdownParser() {
	parserState = 0;
	
}
/**
 * Thread function for the parser thread. Gets the messages from the mailbox and parses them while the exist condition isn't true.
 **/
void* MP_ParserThread(void* args) {
	while (parserState) {
	PMESSAGE msg = MB_getMessage(receivedMsgBuff);
	MP_parseMessage(msg);
	}
	pthread_exit(NULL);
	return 0;
}

/** 
 * Message wiping function. Frees all the memory used by a message structure.
 * @param msg The message structure to wipe.
 **/

void MP_wipeMessage(PMESSAGE msg) {
	free(msg->fullMsg);
	free(msg->data);
	free(msg->dataType);
	free(msg->id);
	free(msg);
}
/**
 * Message decoupler and receiver function. This function reads the messages from the buffers of the sockets and creates a message structure if they are valid. 
 * Can be used in multiple passes by storing all the state values in a structure passed as parameter.
 * @param rec The function parameter structure.
 * @return Returns a message structure if a valid message has been read. Else returns NULL.
 **/
PMESSAGE MP_messageReceiver(MP_PRECEIVERSTR rec) {
	PMESSAGE msg = NULL;
		if (rec->msgLength < rec->maxMsgLength && rec->inMsg == 0) {
			if (((rec->stxPos = strchr(rec->clientBuff, '\x02')) != NULL) && ((rec->etxPos  = strchr(rec->clientBuff, '\x03')) != NULL)) {
				msg = calloc(1, sizeof(MESSAGE));
				MP_initMsgStruc(msg, rec->msgLength);
				strcpy(msg->fullMsg, rec->clientBuff);
				msg->msgSize = strlen(msg->fullMsg);
				memset(rec->clientBuff, 0, rec->bufferLength);
				return msg;
			} else {
				memset(rec->clientBuff, 0, rec->bufferLength);
			}
		} else {
			if (rec->inMsg == 0 && rec->stxFound == 0) {
				if((rec->stxPos = strchr(rec->clientBuff, '\x02')) != NULL) {
					rec->inMsg = 1;
					rec->msgBuff = calloc(rec->bufferLength, sizeof(char));
					strcpy(rec->msgBuff, rec->stxPos);
					memset(rec->clientBuff, 0, rec->bufferLength);
					rec->stxFound = 1;
				} else {
					memset(rec->clientBuff, 0, rec->bufferLength);
				}
			} else if(rec->inMsg == 1 && rec->stxFound == 1) {
				if ((rec->etxPos = strchr(rec->clientBuff, '\x03')) != NULL) {
					rec->msgBuff = realloc(rec->msgBuff, ((strlen(rec->msgBuff)*sizeof(char) + 1) + strlen(rec->clientBuff)));
					strncat(rec->msgBuff, rec->clientBuff, ((rec->etxPos)+1 - rec->clientBuff));
					rec->inMsg = 0;
					rec->stxFound = 0;
					msg = calloc(1, sizeof(MESSAGE));
					MP_initMsgStruc(msg, strlen(rec->msgBuff));
					strcpy(msg->fullMsg, rec->msgBuff);
					msg->msgSize = strlen(msg->fullMsg);
					free(rec->msgBuff);
					return msg;
				} else {
					rec->msgBuff = realloc(rec->msgBuff, ((strlen(rec->msgBuff)*sizeof(char) + 1) + rec->bufferLength));
					strcat(rec->msgBuff, rec->clientBuff);
					memset(rec->clientBuff, 0, rec->bufferLength);
				}
			}
		}
	return NULL;
}
