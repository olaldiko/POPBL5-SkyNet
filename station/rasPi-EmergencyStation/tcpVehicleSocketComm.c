/** @file tcpVehicleSocketComm.c Vehicle connection server related function definitions */

#include "tcpVehicleSocketComm.h"

VSC_STAT vehicleServerStat; /**< Structure containing all the parameters and the status of the vehicle server. */

/**
 * Initialize the vehicle comunication server. 
 * It initializes the needed sockets, binds to an interface and listens for connections.
 **/
void VSC_initVehicleServer() {
	int waitTime = 1;
	vehicleServerStat.state = 1;
	vehicleServerStat.serverSocketStruct.sin_family = AF_INET;
	vehicleServerStat.serverSocketStruct.sin_port = htons(VSC_SRV_PORT);
	vehicleServerStat.serverSocketStruct.sin_addr.s_addr = htonl(INADDR_ANY);
	vehicleServerStat.sockSize = sizeof(struct sockaddr_in);
	while ((vehicleServerStat.serverSocket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
		perror("Error creating the vehicle server socket");
		sleep(waitTime);
		waitTime <<=1;
	}
	
	waitTime = 1;
	//setsockopt(vehicleServerStat.serverSocket, SOL_SOCKET, SO_REUSEADDR, &(int){ 1 }, sizeof(int));
	while (bind(vehicleServerStat.serverSocket, (struct sockaddr*)&vehicleServerStat.serverSocketStruct, (socklen_t)vehicleServerStat.sockSize) < 0) {
		perror("Error binding vehicle server socket");
		sleep(waitTime);
		waitTime <<=1;
	}
	waitTime = 1;
	
	while (listen(vehicleServerStat.serverSocket, VSC_MAXPENDING) < 0) {
		perror("Error when listening connections in vehicle server socket");
		sleep(waitTime);
		waitTime <<=1;
	}
	VSC_acceptConnections();
}

/**
 * Accept the new connections from vehicles. 
 * It accepts the new connection and creates a new thread to listen for the messages from them.
 **/
void VSC_acceptConnections() {
	int clientSock;
	pthread_t *thread;
	while ((clientSock = accept(vehicleServerStat.serverSocket, (struct sockaddr*)&vehicleServerStat.serverSocketStruct, (socklen_t*)&vehicleServerStat.sockSize))) {
		if (clientSock < 0) {
			perror("VSC Error accepting connexion");
		}
		thread = calloc(1, sizeof(pthread_t));
		pthread_create(thread, NULL, VSC_inboundHandlerThreadFunc, (void *)clientSock);
	}
}

/**
 * Shutdown the vehicle comunication server. 
 * It tryes to shutdown the vehicle communication server gracefully by shutting down the sockets,
 * setting an exit condition to the threads and waiting for them to exit.
 **/
void VSC_shutdownVehicleServer() {
	vehicleServerStat.state = 0;
	SA_PVEHICLE_DATA vehicle;
	for (int i = 0; i < SA_countVehiclesInList(); i++) {
		vehicle = SA_getVehicleByIndex(i);
		shutdown(vehicle->clientSocket, SHUT_RDWR);
		close(vehicle->clientSocket);
		pthread_join(vehicle->inboxThread, NULL);
		pthread_join(vehicle->outboxThread, NULL);
	}
	shutdown(vehicleServerStat.serverSocket, SHUT_RDWR);
	close(vehicleServerStat.serverSocket);
	pthread_join(vehicleServerStat.listenThread, NULL);
}

/**
 * Thread to handle the inbound messages from the vehicles. 
 * When a new connection arrives, in the first message sets the handling thread
 * as itself to assign itself as the handling thread to add it to the vehicle structure 
 * after the id is know when parsing the message. 
 * When a disconnection occurs, marks that vehicle as disconnected and the thread exits.
 * @param args The client socket number.
 **/
void* VSC_inboundHandlerThreadFunc(void* args) {
	int clientSock = (int)args;
	int msgLength = 0;
	char *clientBuff = calloc(VSC_SOCKBUF_LEN, sizeof(char));
	MP_PRECEIVERSTR receiver = calloc(1, sizeof(MP_RECEIVERSTR));
	receiver->clientBuff = clientBuff;
	receiver->bufferLength = VSC_SOCKBUF_LEN;
	receiver->maxMsgLength = VSC_MAXRCV_LEN;
	int firstMessage = 1;
	PMESSAGE msg;
	SA_PVEHICLE_DATA vehicle;
	while (((msgLength = (int)recv(clientSock, clientBuff, VSC_MAXRCV_LEN, 0)) > 0) && vehicleServerStat.state == 1) {
		printf("Mensaje recibido: %s, msgLength: %d\n", clientBuff, msgLength);
		receiver->msgLength = msgLength;
		msg = MP_messageReceiver(receiver);
		if (msg != NULL) {
			msg->source = 1;
			if (firstMessage == 1) {
				msg->isFirstMsg = 1;
				msg->clientSocket = clientSock;
				msg->handlingThread = pthread_self();
				firstMessage = 0;
			}
			MB_putMessage(receivedMsgBuff, msg);
		}
	}
	vehicle = SA_searchVehicleByInboxThread(pthread_self());
	if (vehicle != NULL) {
		vehicle->isConnected = 0;
		SA_sendDisconnectedMsg(vehicle->id);
	}
	free(clientBuff);
	free(receiver);
	pthread_exit(NULL);
}

/**
 * Thread to send the messages to the vehicle. 
 * It waits for the messages to arrive at the vehicle's outbox and sends them.
 * @param args The vehicle structure with all his information.
 **/
void* VSC_outboundHandlerThreadFunc(void* args) {
	SA_PVEHICLE_DATA vehicle = (SA_PVEHICLE_DATA)args;
	PMESSAGE msg;
	vehicle->outboxThread = pthread_self();
	while (vehicleServerStat.state == 1) {
		msg = MB_getMessage(vehicle->outbox);
		VSC_SendMessageToVehicle(msg, vehicle);
	}
	pthread_exit(NULL);
}
/**
 * Send a message to a vehicle. It sends a message to the specified vehicle.
 * @param msg The message to be sent.
 * @param vehicle The vehicle to send the message to.
 * @return If the message was correctly sended, returns 0. -1 If an error occurred.
 **/
int VSC_SendMessageToVehicle(PMESSAGE msg, SA_PVEHICLE_DATA vehicle) {
	printf("Enviando mensaje a vehiculo %d-> %s\n", vehicle->id, msg->fullMsg);
	if (send(vehicle->clientSocket, msg->fullMsg, strlen(msg->fullMsg), 0) == -1) {
		perror("Error sending message");
		return -1;
	} else {
		MP_wipeMessage(msg);
		return 0;
	}
}
