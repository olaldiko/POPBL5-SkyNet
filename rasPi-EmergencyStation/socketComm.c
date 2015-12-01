//
//  socketComm.c
//  rasPi-EmergencyStation
//
//  Created by Gorka Olalde Mendia on 1/12/15.
//  Copyright © 2015 Gorka Olalde Mendia. All rights reserved.
//

#include "socketComm.h"





int SC_serverStartup(int port) {
    int sockSize = sizeof(struct sockaddr_in);
    serverSocketStruct.sin_family = AF_INET;
    serverSocketStruct.sin_port = htons(port);
    serverSocketStruct.sin_addr.s_addr = htonl(INADDR_ANY);
    serverBuffer = calloc(SRV_MAXBUFF, sizeof(char));
    
    if ((serverSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1) return -1;
    if (bind(serverSocket, (struct sockaddr*)&serverSocketStruct, sockSize) == -1) return -1;
    return 0;
}

int SC_listenMessages() {
    int state = 1;
    int recv_length = 0;
    pthread_t* receiverThread;
    MESSAGE msg;
    struct sockaddr_in clientSocket;
    while (state) {
        if((recv_length = recvfrom(serverSocket, serverBuffer, SRV_MAXBUFF, 0, (struct sockaddr*)&clientSocket, &sockSize)) == -1) {
           state = -1;
        } else {
            MP_initMsgStruc(&msg, recv_length);
            msg.source = 1;
            msg.msgSize = recv_length;
            strcpy(msg.fullMsg, serverBuffer);
            receiverThread = calloc(1, sizeof(pthread_t));
            pthread_create(receiverThread, NULL, SC_receiverThreadFunc, (void*)&msg);
            //TO-DO: Add a thread dynamic queue to track them and close and free their resources later.
        }
    }
    return state;
}

void* SC_receiverThreadFunc(void* args) {
    SC_treatIncomingMsg((PMESSAGE)args);
    pthread_exit(NULL);
}

void SC_treatIncomingMsg(PMESSAGE msg) {
    MP_parseMessage(msg);
    //TO-DO: ACK managing
}

int SC_sendMessage(PMESSAGE msg) {
    sendto(serverSocket, msg->fullMsg, msg->msgSize, 0, (struct sockaddr*)&msg->clientSocket, sockSize);
    if(msg->msgType == 2) {
        if (msg->waitACK) {
            
            //TO-DO: ACK managing
            
        }
    }
}
