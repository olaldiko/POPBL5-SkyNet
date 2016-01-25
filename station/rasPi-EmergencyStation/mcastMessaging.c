/** @file mcastMessaging.c FIle containing all the definition of the functions related to multicast communication. */

#include "mcastMessaging.h"

MCM_serverStats mcmServerStats;

/**
 * Intialize the multicast UDP listener and sender. 
 * Initializes all the sockets, threads and structures needed for the multicast communications.
 **/
void MCM_initMcastServer() {
	mcmServerStats.state = 1;
	MCM_threadStruct *general = calloc(1, sizeof(MCM_threadStruct));
	MCM_threadStruct *station = calloc(1, sizeof(MCM_threadStruct));
	mcmServerStats.generalInbox = MB_initBuffer(10);
	mcmServerStats.stationOutbox = MB_initBuffer(10);
	MCM_initSockets();
	MCM_initGeneralGroup();
	MCM_initStationGroup();
	general->socket = mcmServerStats.generalSocket;
	general->buffer = mcmServerStats.generalInbox;
	general->address.sin_addr.s_addr = inet_addr(MCM_GENERAL_GRP);
	general->address.sin_family = AF_INET;
	general->address.sin_port = htons(MCM_GEN_PORT);
	station->socket = mcmServerStats.stationSocket;
	station->buffer = mcmServerStats.stationOutbox;
	station->address.sin_addr.s_addr =  mcmServerStats.stationGroup.s_addr;
	station->address.sin_family = AF_INET;
	station->address.sin_port = htons(MCM_STATION_PORT);
	pthread_create(&mcmServerStats.listenThread, NULL, MCM_listenerThread, (void*)general);
	pthread_create(&mcmServerStats.sendThread, NULL, MCM_senderThread, (void*)station);
}

/**
 * Close the sockets and stop all threads of multicast comunication.
 * Gracefully shutdowns and then closes all the involved sockets. Then, stops the listener and sender thread.
 **/
void MCM_shutdownMcastServer() {
	mcmServerStats.state = 0;
	shutdown(mcmServerStats.generalSocket, SHUT_RDWR);
	close(mcmServerStats.generalSocket);
	shutdown(mcmServerStats.stationSocket, SHUT_RDWR);
	close(mcmServerStats.stationSocket);
	pthread_join(mcmServerStats.listenThread, NULL);
	pthread_join(mcmServerStats.sendThread, NULL);
}

/**
 * Initialize the sockets for multicast communication.
 * Initializes both the sockets to listen to the general group and the socket to send the messages to the station group.
 **/
void MCM_initSockets() {
	int yes = 1;
	if ((mcmServerStats.generalSocket = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		perror("Error creating general MCAST socket");
	}
	if ((mcmServerStats.stationSocket  = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		perror("Error creating station MCAST socket");
	}
	
	if (setsockopt(mcmServerStats.generalSocket, SOL_SOCKET, SO_REUSEPORT, &yes, sizeof(int)) < 0) {
		perror("Error seting MCAST sock options");
	}
	if (setsockopt(mcmServerStats.stationSocket, SOL_SOCKET, SO_REUSEPORT, &yes, sizeof(int)) < 0) {
		perror("Error seting MCAST sock options");
	}
}

/**
 * Caltulate address for the multicast group.
 * Calculates the group address based on the ID of the station. The address is changed to little endian first to add the id value. Then is converted back.
 * @return The in_addr structure with the address.
 **/
struct in_addr MCM_calcStationAddress() {
	struct in_addr address;
	inet_aton(MCM_GENERAL_GRP, &address);
	
	address.s_addr = ((address.s_addr>>24)&0xff) | ((address.s_addr<<8)&0xff0000) | ((address.s_addr>>8)&0xff00) | ((address.s_addr<<24)&0xff000000);
	address.s_addr += stationID;
	address.s_addr = ((address.s_addr>>24)&0xff) | ((address.s_addr<<8)&0xff0000) | ((address.s_addr>>8)&0xff00) | ((address.s_addr<<24)&0xff000000);
	return address;
}

/**
 * Sets the station group to the one that we have calculated first.
 * @see MCM_calcStationAddress
 **/

void MCM_initStationGroup() {
	mcmServerStats.stationGroup = MCM_calcStationAddress();

}

/**
 * Initialize the general groups. Sets the kernel to join the general multicast group.
 **/
void MCM_initGeneralGroup() {
	struct sockaddr_in bindSock;
	//inet_aton("172.17.16.252", &bindSock.sin_addr);
	bindSock.sin_addr.s_addr = htonl(INADDR_ANY);
	bindSock.sin_family = AF_INET;
	bindSock.sin_port = htons(MCM_GEN_PORT);
	if (bind(mcmServerStats.generalSocket, (struct sockaddr*)&bindSock, sizeof(struct sockaddr_in))) {
		perror("Error binding general MCAST socket");
	}
	inet_aton(MCM_GENERAL_GRP, &mcmServerStats.generalMreq.imr_multiaddr);
	mcmServerStats.generalMreq.imr_interface.s_addr = htonl(INADDR_ANY);

	if ((setsockopt(mcmServerStats.generalSocket, IPPROTO_IP, IP_ADD_MEMBERSHIP, &mcmServerStats.generalMreq, sizeof(struct ip_mreq))) < 0) {
		perror("Error joining MCAST group");
	}
	
}


/**
 * Thread for listening to messages coming from the general multicast group. 
 * This thread waits for new messages to be received. Then puts the valid messages into the parser's inbox.
 * @param args Receives a the socket and the address of the group via structure.
 **/
void* MCM_listenerThread(void* args) {
	struct sockaddr_in address;
	int structSize = sizeof(struct sockaddr_in);
	MCM_threadStruct* opts = (MCM_threadStruct *)args;
	PMESSAGE msg;	
	char* buffer = calloc(MCM_BUFFSIZE, sizeof(char));
	MP_PRECEIVERSTR receiver = calloc(1, sizeof(MP_RECEIVERSTR));
	receiver->clientBuff = buffer;
	receiver->bufferLength = MCM_BUFFSIZE; 
	receiver->maxMsgLength = MCM_MAXMSGSIZE;
	while ((receiver->msgLength = recvfrom(opts->socket, buffer, MCM_MAXMSGSIZE, 0, (struct sockaddr *)&address, &structSize)) != -1 && mcmServerStats.state == 1) {
		msg = MP_messageReceiver(receiver);
		if (msg != NULL) {
			msg->source = 0;
			printf("Mensage from general multicast group received: %s\n", msg->fullMsg);
			MB_putMessage(receivedMsgBuff, msg);
		}
	}
	free(buffer);
	pthread_exit(NULL);
}

/**
 * Thread for sending the messages to the station multicast group.
 * Waits for messages in the outbox of the station group ands sends those messages.
 **/

void* MCM_senderThread(void* args) {
	MCM_threadStruct *opts = (MCM_threadStruct *)args;
	PMESSAGE msg;
	int structSize = sizeof(struct sockaddr_in);
	while (mcmServerStats.state == 1) {
		msg = MB_getMessage(opts->buffer);
		printf("Message %s sent via multicast group\n", msg->fullMsg);
		if ((sendto(opts->socket, msg->fullMsg, strlen(msg->fullMsg), 0, (struct sockaddr *)&opts->address, structSize)) < 0) {
			perror("Error sending via MCAST");
		}
	}
	pthread_exit(NULL);
}