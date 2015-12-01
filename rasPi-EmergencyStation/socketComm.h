//
//  socketComm.h
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 1/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#ifndef socketComm_h
#define socketComm_h

#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <pthread.h>
#include <semaphore.h>
#include "messageParser.h"

#define SRV_PORT 5000
#define SRV_MAXBUFF 5000




int serverSocket = 0;
struct sockaddr_in serverSocketStruct;
int sockSize = sizeof(struct sockaddr_in);
char* serverBuffer;

int SC_serverStartup(int port);
int SC_listenMessages();
void* SC_receiverThreadFunc(void* args);
void SC_treatIncomingMsg(PMESSAGE msg);

#endif /* socketComm_h */
