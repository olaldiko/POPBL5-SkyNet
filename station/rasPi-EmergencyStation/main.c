//
//  main.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 30/11/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include <stdio.h>
#include <signal.h>
#include "structures.h"
#include "messageParser.h"
#include "msgBuffers.h"
#include "stationActions.h"
#include "tcpServerSocketComm.h"
#include "tcpVehicleSocketComm.h"

int exitCond = 0;
void* mcastConnControlThread(void* args);
void* serverConnControlThread(void* args);
void* vehicleConnControlThread(void* args);

void sig_handler(int signo) {
	if (signo == SIGINT) {
		printf("SIGINT received\n");
		exitCond = 1;
	}
}

int main(int argc, const char * argv[]) {
	pthread_t mcastConn;
	pthread_t serverConn;
	pthread_t vehicleConn;
	signal(SIGINT, sig_handler);
	signal(SIGTERM, sig_handler);
	MP_initParser();
	pthread_create(&mcastConn, NULL, mcastConnControlThread, NULL);
	pthread_create(&serverConn, NULL, serverConnControlThread, NULL);
	pthread_create(&vehicleConn, NULL, vehicleConnControlThread, NULL);
	while (!exitCond) {
		sleep(1);
	}
	pthread_join(serverConn, NULL);
	pthread_join(vehicleConn, NULL);
	
    return 0;
}

void* mcastConnControlThread(void* args) {
	MCM_initMcastServer();
	while (!exitCond) {
		sleep(1);
	}
	MCM_shutdownMcastServer();
	pthread_exit(NULL);
}

void* serverConnControlThread(void* args) {
	SSC_initServerConnection();
	while (!exitCond) {
		sleep(1);
	}
	SSC_stopServerConn();
	pthread_exit(NULL);
}
void* vehicleConnControlThread(void* args) {
	VSC_initVehicleServer();
	while (!exitCond) {
		sleep(1);
	}
	VSC_shutdownVehicleServer();
	pthread_exit(NULL);
}