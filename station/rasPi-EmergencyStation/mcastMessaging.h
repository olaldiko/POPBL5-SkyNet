/** @file mcastMessaging.h FIle containing all the headers of the functions related to multicast communication. */

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

#define MCM_GEN_PORT 7000 /**< Port to listen to the general group. */
#define MCM_STATION_PORT 8000 /**< Port to send the messages to the station group. */
#define MCM_GENERAL_GRP "239.128.0.1" /**< Address of the general group. */
#define MCM_BUFFSIZE 1024 /**< Buffer size for the incoming messages. */
#define MCM_MAXMSGSIZE 1020 /**< Maximum message size for the incoming messages. (Each pass) */


extern MCM_serverStats mcmServerStats; /**< Variable to store all the multicast server info. */


void MCM_initMcastServer();
void MCM_shutdownMcastServer();
void MCM_initSockets();
struct in_addr MCM_calcStationAddress();
void MCM_initStationGroup();
void MCM_initGeneralGroup();
void* MCM_listenerThread(void* args);
void* MCM_senderThread(void* args);