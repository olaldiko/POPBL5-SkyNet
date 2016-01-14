//
//  mcastMessaging.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 12/1/16.
//  Copyright © 2016 Gorka Olalde Mendia. All rights reserved.
//

#ifndef mcastMessaging_h
#define mcastMessaging_h

#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "structures.h"
#include "msgBuffers.h"

#endif /* mcastMessaging_h */

#define MCM_GEN_PORT 7000
#define MCM_STATION_PORT 8000
#define MCM_GENERAL_GRP "239.128.0.1"
#define MCM_BUFFSIZE 1024


extern MCM_serverStats mcmServerStats;
void MCM_initMcastServer();
void MCM_shutdownMcastServer();
void MCM_initSockets();
struct in_addr MCM_calcStationAddress();
void MCM_initStationGroup();
void MCM_initGeneralGroup();
void* MCM_listenerThread(void* args);
void* MCM_senderThread(void* args);