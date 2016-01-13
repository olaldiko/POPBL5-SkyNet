//
//  tcpVehicleSocketComm.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 9/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#ifndef tcpVehicleSocketComm_h
#define tcpVehicleSocketComm_h

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include "structures.h"
#include "msgBuffers.h"
#include "messageParser.h"
#include "stationActions.h"


#define VSC_SRV_PORT 6000
#define VSC_MAXPENDING 10
#define VSC_SOCKBUF_LEN 512
#define VSC_MAXRCV_LEN 510


void VSC_initVehicleServer();
void VSC_acceptConnections();
void VSC_shutdownVehicleServer();

void* VSC_inboundHandlerThreadFunc(void* args);
void* VSC_outboundHandlerThreadFunc(void* args);

int VSC_SendMessageToVehicle(struct MESSAGE *msg, struct SA_VEHICLE_DATA *vehicle);

extern VSC_STAT vehicleServerStat;

#endif /* tcpVehicleSocketComm_h */