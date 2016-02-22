/** @file tcpServerSocketComm.c Server connection related function headers */

#ifndef tcpServerSocketComm_h
#define tcpServerSocketComm_h

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

#define SSC_SRV_ADDRESS "ayerdi" /**< server's domain name.*/
#define SSC_SRV_PORT 5000 /**< server port. */
#define SSC_SRV_BUFLEN 1024 /**< Receive buffer's size. */
#define SSC_RCV_MAXLEN 1020 /**< Max size of the received message. (Each pass) */

extern SSC_STAT serverSocketStat;

extern PMSGBUFF SSC_serverSendBuffer;


void SSC_makeServerConnection();
void SSC_initServerConnection();
void SSC_initBuffers();
void SSC_sendMessageToServer(struct MESSAGE *msg);
void SSC_stopServerConn();
void SSC_initServerConnThreads();
void* SSC_msgSenderThreadFunc(void* args);
void* SSC_msgListenerThreadFunc(void* args);

#endif /* tcpServerSocketComm_h */
