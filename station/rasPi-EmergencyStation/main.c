/** @file main.c Main file and functions of the emergency station application. */

#include "main.h"

int exitCond = 0; /**< Global to store the exit condition of the application. */
void* mcastConnControlThread(void* args); /**< Thread for controlling the multicast server initialization and shutdown. */
void* serverConnControlThread(void* args); /**< Thread for controlling the server client initialization and shutdown. */
void* vehicleConnControlThread(void* args); /**< Thread for controlling the vehicle server initialization and shutdown. */


/**
 * Signal handler of the application. Catches the SIGTERM and SIGINT signals and sets the exit condition of the application.
 **/
void sig_handler(int signo) {
	if (signo == SIGINT || signo == SIGTERM) {
		printf("SIGINT or SIGTERM received\n");
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

/**
 * Thread to control the initialization and shutdown of the multicast server.
 **/
void* mcastConnControlThread(void* args) {
	MCM_initMcastServer();
	while (!exitCond) {
		sleep(1);
	}
	MCM_shutdownMcastServer();
	pthread_exit(NULL);
}

/**
 * Thread for controlling the communications to the server initialization and shutdown.
 **/
void* serverConnControlThread(void* args) {
	SSC_initServerConnection();
	while (!exitCond) {
		sleep(1);
	}
	SSC_stopServerConn();
	pthread_exit(NULL);
}

/**
 *  Thread for controlling the vehicle communication server initialization and shutdown.
**/
void* vehicleConnControlThread(void* args) {
	VSC_initVehicleServer();
	while (!exitCond) {
		sleep(1);
	}
	VSC_shutdownVehicleServer();
	pthread_exit(NULL);
}