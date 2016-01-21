/** @file stationActions.c FIle containing all the station action definitions. */
#include "stationActions.h"

SA_VEHICLE_LIST vehicleList;
int stationID = 1;

void SA_treatIDMessage(PMESSAGE msg) {
	
}

/**
 * Function to treat the ID Request Messages. Generates the IDrequest message with the station ID as the 
 * ID and the vehicle socket number in the message field.
 * @param msg The message structure.
 **/
void SA_treatIDReqMessage(PMESSAGE msg) {
	sprintf(msg->fullMsg, "\x02%d\x1dIDREQUEST\x1d%d\x03", stationID, msg->clientSocket);
	MB_putMessage(SSC_serverSendBuffer, msg);
}

/**
 * Function to treat the ID Assign messages. It search for the vehicle with the corresponding socket no, 
 * establishes the ID in the vehicle list and finally forwards the ID assign message to the vehicle.
 * @param msg The structure of the received message.
 **/

void SA_treatIDAssign(PMESSAGE msg) {
	SA_PVEHICLE_DATA vehicle;
	if ((vehicle = SA_searchVehicleBySocket(atoi(msg->data))) != NULL) {
		vehicle->id = atoi(msg->id);
		MB_putMessage(vehicle->outbox, msg);
	}
}

/**
 * Function to treat the Location messages. Fowrwards the location messages sent by the vehicles to the server.
 * @param msg The received message structure.
 **/

void SA_treatLOCMessage(PMESSAGE msg) {
	MB_putMessage(SSC_serverSendBuffer, msg);
}

/** Function to treat the initial vehicle list message. 
 * Receives a vehicle list from the server, and adds these vehicles to the station's vehicle list.
 * @param msg The received message structure.
 **/

void SA_treatListMessage(PMESSAGE msg) {
	char* vehicle;
	while((vehicle = strtok(msg->data, "$")) != NULL) {
		SA_addVehicleToList(atoi(vehicle));
	}
}

/**
 * Function to treat the route messages sent by the server.
 * Receives the route message, search for the referred message addressee and forwards the message. 
 * If the vehicle is not found. It sends a NACK to the server.
 * @param msg The received message structure.
 *
 **/

void SA_treatRouteMessage(PMESSAGE msg) {
	assert(msg->fullMsg != NULL);
	SA_PVEHICLE_DATA vehicle = SA_searchVehicleById(atoi(msg->id));
	PMESSAGE ack;
	if (vehicle == NULL) {
		ack = calloc(1, sizeof(MESSAGE));
		MP_initMsgStruc(ack, 100);
		sprintf(ack->fullMsg, "\x02%s\x1dNACK\x1d%s", msg->id, msg->msgCounter);
		MB_putMessage(SSC_serverSendBuffer, ack);
	} else {
		MB_putMessage(vehicle->outbox, msg);
	}
}

/**
 * Function to initialize a new vehicle structure. Assigns the id passed by parameter to the vehicle, 
 * initializes the outbox and creates a new thread to wait for messages from it.
 * @param vehicle The vehicle structure pointer to initialize.
 * @param id The id that will be assigned to the vehicle.
 **/

void SA_initVehicle(SA_PVEHICLE_DATA vehicle, int id) {
	vehicle->id = id;
	vehicle->outbox = MB_initBuffer(10);
	pthread_create(&vehicle->outboxThread, NULL, VSC_outboundHandlerThreadFunc, vehicle);
}

/**
 * Function to treat the Status messages coming from the vehicle. 
 * Receives the status message and forwards it to the server send buffer.
 * @param msg The received message.
 **/
void SA_treatStatusMessage(PMESSAGE msg) {
	MB_putMessage(SSC_serverSendBuffer, msg);
}

/**
 * Function to treat the Alert messages coming from the server.
 * Receives the message by parameter and forwards it to the outbox of the station multicast group.
 * @param msg The received message structure.
 **/
void SA_treatAlertMessage(PMESSAGE msg) {
	MB_putMessage(mcmServerStats.stationOutbox, msg);
}


/**
 * Vehicle list initializer.
 * Initializes the vehicle list by creating the mutex element and setting the element number to 0.
 **/
void SA_initVehicleList() {
	pthread_mutex_init(&vehicleList.mtx, NULL);
	vehicleList.numElems = 0;
	vehicleList.head = NULL;
}

/**
 * Add a new vehicle to the vehicle list. 
 * Adds the vehicle with the corresponding id to the list and returns the pointer to that newly created vehicle.
 * @param id The vehicle ID to assign.
 * @return The pointer to the newly created vehicle.
 **/
SA_PVEHICLE_DATA SA_addVehicleToList(int id) {
	pthread_mutex_lock(&vehicleList.mtx);
	SA_PVEHICLE_ELEM element = calloc(1, sizeof(SA_VEHICLE_ELEM));
	SA_initVehicle(&element->vehicle, id);
	SA_PVEHICLE_ELEM queueCursor;
	element->vehicle.id = id;
	if(vehicleList.head == NULL) {
		vehicleList.head = element;
	} else {
		for (queueCursor = vehicleList.head; queueCursor->next != NULL; queueCursor = queueCursor->next);
		queueCursor->next = element;
	}
	element->vehicle.outbox = MB_initBuffer(10);
	pthread_mutex_unlock(&vehicleList.mtx);
	return &element->vehicle;
}

/**
 * Search a vehicle by socket number. 
 * Searchs a vehicle that has the same socket number that the one passed by parameter.
 * @param socket The socket to search.
 * @return The pointer to the vehicle if found. NULL if the vehicle is not found.
 **/
SA_PVEHICLE_DATA SA_searchVehicleBySocket(int socket) {
	SA_PVEHICLE_ELEM queueCursor;
	SA_PVEHICLE_DATA retVal = NULL;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		retVal = NULL;
	} else {
		for (queueCursor = vehicleList.head; ((queueCursor->next != NULL) && (queueCursor->vehicle.clientSocket != socket)); queueCursor = queueCursor->next);
		if (queueCursor->vehicle.clientSocket == socket) {
			retVal = &queueCursor->vehicle;
		} else {
			retVal = NULL;
		}
	}
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}

/**
 * Search a vehicle by ID.
 * Searchs a vehicle that has the specified ID in the vehicle list.
 * @param id The vehicle ID to search.
 * @return The pointer to the vehicle if found. NULL if the vehicle is not found.
 **/
SA_PVEHICLE_DATA SA_searchVehicleById(int id) {
	SA_PVEHICLE_ELEM queueCursor;
	SA_PVEHICLE_DATA retVal = NULL;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		retVal = NULL;
	} else {
		if (id > 0) { //We search by ID
			for (queueCursor = vehicleList.head; ((queueCursor->next != NULL) && (queueCursor->vehicle.id != id)); queueCursor = queueCursor->next);
			 if (queueCursor->vehicle.id == id) {
				 retVal = &queueCursor->vehicle;
			 } else {
				 retVal = NULL;
			 }
		}
	}
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}

/**
 * Search a vehicle by it's inbox handling thread.
 * @param inboxThread The threading reading inbound messages.
 * @return The pointer to the vehicle element if found. Returns else if not found.
 **/
SA_PVEHICLE_DATA SA_searchVehicleByInboxThread(pthread_t inboxThread) {
	SA_PVEHICLE_ELEM queueCursor;
	SA_PVEHICLE_DATA retVal;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		retVal = NULL;
	} else {
		for (queueCursor = vehicleList.head; ((queueCursor->next != NULL) && (queueCursor->vehicle.inboxThread = inboxThread)); queueCursor = queueCursor->next);
		if (queueCursor->vehicle.inboxThread == inboxThread) {
			retVal = &queueCursor->vehicle;
		} else {
			retVal = NULL;
		}
	}
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}

/**
 * Count the vehicles on the vehicle list.
 * Counts the vehicle amount in the vehicle list and returns th quantity.
 * @return The number of vehicles on the list.
 **/
int SA_countVehiclesInList() {
	int retVal = 0;
	SA_PVEHICLE_ELEM queueCursor;
	pthread_mutex_lock(&vehicleList.mtx);
	if (vehicleList.head == NULL) {
		return 0;
	}
	for (queueCursor = vehicleList.head; queueCursor != NULL; queueCursor = queueCursor->next, retVal++);
	pthread_mutex_unlock(&vehicleList.mtx);
	return retVal;
}

/**
 * Get the vehicle that is on the n-th position in the list. 
 * Receives the list index by parameter and returns the vehicle on that index if it's found. 
 * Returns NULL if not found.
 * @param index The index to get the vehicle from.
 * @return The pointer to the vehicle element if it's found. NULL if not found.
 **/
SA_PVEHICLE_DATA SA_getVehicleByIndex(int index) {
	int i = 0;
	SA_PVEHICLE_ELEM queueCursor;
	pthread_mutex_lock(&vehicleList.mtx);
	for (queueCursor = vehicleList.head, i = 0; i < index && queueCursor != NULL; queueCursor = queueCursor->next, i++);
	pthread_mutex_unlock(&vehicleList.mtx);
	return &queueCursor->vehicle;
}

/**
 * Send the vehicle connected message to the server. 
 * This function builds the connected message by receiving the vehicle id by parameter, building the message
 * and putting it into the server outbox.
 * @param id The vehicle ID.
 **/
void SA_sendConnectedMsg(int id) {
	PMESSAGE msg = calloc(1, sizeof(MESSAGE));
	MP_initMsgStruc(msg, 100);
	sprintf(msg->fullMsg, "\x02%d%sCONNECTED\x1d%d\x03", id, "\x1d",stationID);
	MB_putMessage(SSC_serverSendBuffer, msg);
}

/**
 * Send the vehicle disconnected message to the server.
 * This function receives the vehicle ID by parameter, builds the message and puts it in the server outbox.
 * @param id The vehicle ID.
 **/
void SA_sendDisconnectedMsg(int id) {
	PMESSAGE msg = calloc(1, sizeof(MESSAGE));
	MP_initMsgStruc(msg, 100);
	sprintf(msg->fullMsg, "\x02%d%sDISCONNECTED\x1d%d\x03", id, "\x1d",stationID);
	MB_putMessage(SSC_serverSendBuffer, msg);
}

/**
 * Send the Join multicast group message to the vehicle.
 * When a vehicle connects, this function sends the message to that vehicle by getting the ID by parameter,
 * getting the multicast group from the station, 
 * building the message and then putting it into the vehicles outbox.
 * @param id The vehicle ID.
 **/

void SA_sendJoinMessage(int id) {
	SA_PVEHICLE_DATA vehicle = SA_searchVehicleById(id);
	PMESSAGE msg = calloc(1, sizeof(MESSAGE));
	MP_initMsgStruc(msg, 100);
	if (vehicle != NULL) {
		sprintf(msg->fullMsg, "\x02%d\x1dJOIN\x1d%s\x03", stationID, inet_ntoa(mcmServerStats.stationGroup));
		MB_putMessage(vehicle->outbox, msg);
	}
}

