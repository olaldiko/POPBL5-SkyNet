/** @file tcpVehicleSocketComm.h Vehicle connection server's headers */

#ifndef tcpVehicleSocketComm_h
#define tcpVehicleSocketComm_h

#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include "structures.h"
#include "msgBuffers.h"


#define VSC_SRV_PORT 6000 /**< Port of where the vehicle connection server will listen definition*/
#define VSC_MAXPENDING 10 /**< Max pending messages definition*/
#define VSC_SOCKBUF_LEN 512 /**< Size of the receiver buffer */
#define VSC_MAXRCV_LEN 510 /**< Max size of the received messages stored in the buffer */


void VSC_initVehicleServer();
void VSC_acceptConnections();
void VSC_shutdownVehicleServer();

void* VSC_inboundHandlerThreadFunc(void* args);
void* VSC_outboundHandlerThreadFunc(void* args);

int VSC_SendMessageToVehicle(struct MESSAGE *msg, struct SA_VEHICLE_DATA *vehicle);

extern VSC_STAT vehicleServerStat;

#endif /* tcpVehicleSocketComm_h */