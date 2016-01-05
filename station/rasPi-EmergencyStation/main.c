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
void sig_handler(int signo) {
	if (signo == SIGINT) {
		exitCond = 1;
	}
}

int main(int argc, const char * argv[]) {
	signal(SIGINT, sig_handler);
	SSC_initServerConnection();
	VSC_initVehicleServer();
	while (!exitCond) {
		sleep(1);
	}
	VSC_shutdownVehicleServer();
	SSC_stopServerConn();
	
    return 0;
}
