//
//  messageParser.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 30/11/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "messageParser.h"

void MP_initMsgStruc(PMESSAGE msg, int msgSize) {
    msg->source   = 0;
    msg->msgSize     = msgSize;
    msg->fullMsg     = calloc(msgSize, sizeof(char));
    msg->id          = calloc(MSG_IDSIZE, sizeof(char));
    msg->dataType        = calloc(MSG_TYPESIZE, sizeof(char));
    msg->data        = calloc(msgSize-MSG_IDSIZE-MSG_TYPESIZE, sizeof(char));
    msg->waitACK     = 0;
    msg->ackReceived = 0;
}

int MP_parseMessage(PMESSAGE msg) {
    char *strippedMsg = calloc(msg->msgSize, sizeof(char));
    
    if((strcspn(msg->fullMsg, "\x02") == 0) && (strcspn(msg->fullMsg, "\x03") == strlen(msg->fullMsg))) {
            strcpy(strippedMsg, msg->fullMsg +1);
            strippedMsg[strlen(strippedMsg)-1] = '\x00';
            strcpy(msg->id, strtok(strippedMsg, "\x02\x03"));
            strcpy(msg->dataType, strtok(NULL, "\x02\x03"));
            strcpy(msg->data, strtok(NULL, "\x02\x03"));
        if (msg->source == 0) {
            if(strcmp(msg->dataType, "RUT"))            MP_parseRouteMessage(msg);
            else if (strcmp(msg->dataType, "ALT"))      MP_parseServerAlert(msg);
            else if (strcmp(msg->dataType, "ACK"))      MP_parseServerACK(msg);
            else if (strcmp(msg->dataType, "NACK"))     MP_parseServerNACK(msg);
            
        } else {
            if(strcmp(msg->dataType, "ID"))             MP_parseVehicleID(msg);
            else if (strcmp(msg->dataType, "IDREQ"))    MP_parseVehicleIDRequest(msg);
            else if (strcmp(msg->dataType, "LOC"))      MP_parseVehicleLocation(msg);
            else if (strcmp(msg->dataType, "STAT"))     MP_parseVehicleStat(msg);
            else if (strcmp(msg->dataType, "ACK"))      MP_parseVehicleACK(msg);
            else if (strcmp(msg->dataType, "NACK"))     MP_parseVehicleNACK(msg);
        }
        return 0;
    } else {
        return -1;
    }
}


