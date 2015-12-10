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

#endif /* tcpVehicleSocketComm_h */

typedef struct VSC_STAT {
	int serverSocket;
	struct sockaddr_in serverSocketStruct;
	struct hostent *serverInfo;
	int sockSize;
	char* buffer;
	int state;
	pthread_t listenThread;
	pthread_t sendThread;
}VSC_STAT, *VSC_PSTAT;

VSC_STAT vehicleServerStat;
