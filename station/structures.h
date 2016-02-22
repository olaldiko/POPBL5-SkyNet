/** @file structures.h File containing all the structure definitions used in the other modules. */

#ifndef structures_h
#define structures_h
#include <pthread.h>
#include <netinet/in.h>

//messageparser.h

/**
 * Structure used in the message receiver function to store it's state.
 **/
typedef struct MP_RECEIVERSTR{
	int msgLength; /**< Lenght of the last received message. */
	char *clientBuff; /**< Pointer to the client buffer's start address. */
	char *msgBuff; /**< Pointer to the receiver's internal buffer. */
	int bufferLength; /**< Lenght of the socket's buffer. */
	int maxMsgLength; /**< Maximum lenght of the message stored in the socket's buffer. */
	char* stxPos; /**< Position of the STX character in the message. */
	char* etxPos; /**< Position of the EXT character in the message. */
	int inMsg; /**< State of the reading process of the message. */
	int stxFound; /**< Tells if the STX character has been found or not. */
}MP_RECEIVERSTR, *MP_PRECEIVERSTR;

/**
 * Structure where the messages are stored.
 **/
typedef struct MESSAGE{
	int source; /**< Source of the message. 0 - Sever, 1 - Vehicle. */
	in_addr_t srcAddress; /**< Source address of the message. (Unused) */
	int clientSocket; /**< Socket where the message come from. */
	pthread_t handlingThread; /**< Thread that has handled the incoming message. */
	int isFirstMsg; /**< Tells if this is the first received message from a vehicle. */
	int msgSize; /**< Size of the message. */
	char* fullMsg; /**< Full message with all the fields on it. */
	char* id; /**< ID field of the message. */
	char* dataType; /**< Type field of the message. */
	char* data; /**< Data field of the message. */
	char* msgCounter; /**< Counter field of the message. (Unused) */
}MESSAGE, *PMESSAGE;


//msgBuffers.h

/**
 * Node element structure of the message mailbox/buffer.
 **/
typedef struct MSGQUEUE {
	struct MESSAGE *msg; /**< Message stored in the node. */
	struct MSGQUEUE *next; /**< Pointer to the next node in the queue. */
}MSGQUEUE, *PMSGQUEUE;


/**
 * Message mailbox/buffer main structure.
 **/
typedef struct MSGBUFF {
	pthread_mutex_t mtx; /**< Mutex used for single thread access to the mailbox. */
	pthread_cond_t empty; /**< Empty condition. */
	pthread_cond_t full; /**< Full condition. */
	int cant; /**< Quantity of elements in the mailbox. */
	int maxVals; /**< Maximum values that can be stored in the mailbox. */
	PMSGQUEUE head; /**< Head node of the mailbox. */
}MSGBUFF, *PMSGBUFF;


//stationActions.h

/**
 * Structure to store the vehicles connected to the station.
 **/
typedef struct SA_VEHICLE_DATA {
	int id; /**< The vehicle ID. */
	int clientSocket; /**< The socket where the vehicle is connected. */
	int isConnected; /**< State of the vehicle. 1 - Connected, 0 - Disconnected. */
	struct sockaddr_in clientSocketStruct; /**< Socket struct of the connected vehicle. */
	PMSGBUFF outbox; /**< Outbox to store the messages to be sent to the vehicle. */
	pthread_t inboxThread; /**< Thread handling the messages coming from the vehicle. */
	pthread_t outboxThread; /**< Thread handling the messages to be sent to the vehicle. */
}SA_VEHICLE_DATA, *SA_PVEHICLE_DATA;

/**
 * Node element structure of the vehicle list.
 **/
typedef struct SA_VEHICLE_ELEM {
	SA_VEHICLE_DATA vehicle; /**< The stored vehicle. */
	struct SA_VEHICLE_ELEM* next; /**< The next node. */
}SA_VEHICLE_ELEM, *SA_PVEHICLE_ELEM;

/**
 * Structure of the queue to store all the vehicles.
 **/
typedef struct SA_VEHICLE_QUEUE {
	pthread_mutex_t mtx;
	SA_PVEHICLE_ELEM head;
	int numElems;
}SA_VEHICLE_LIST, *SA_PVEHICLE_QUEUE;


//tcpServerSocketComm.h

/**
 * Structure to store all the parameters used by the connection to the server.
 **/
typedef struct SSC_STAT {
	int serverSocket; /**< Server where the server is connected. */
	struct sockaddr_in serverSocketStruct; /**< Structure containing the server address and the port. */
	struct hostent *serverInfo; /**< Hostent structure where the addresses obtaining when doing the name resolution are stored. */
	struct sockaddr_in clientSocketStruct; /**< Client socket structure. */
	int sockSize; /**< Size of the socket structure. */
	char* buffer; /**< Pointer to the buffer where to store the incoming data. */
	int state; /**< State of the server connection service. Is used by the threads to know when they have to exit. 0 - Stop, 1 - Active */
	pthread_t listenThread; /**< Thread listening to the incoming messages from the server. */
	pthread_t sendThread; /**< Thread for sending the messages to the server. */
}SSC_STAT, *SSC_PSTAT;

//tcpVehicleSocketComm.h
/**
 * Structure to store the parameters of the vehicle communication server. 
 **/
typedef struct VSC_STAT {
	int state; /**< State of the vehicle connection server. Used by the threads to know when they have to exit. 0 - Stop, 1 - Active */
	int serverSocket; /**< Server socket number. */
	struct sockaddr_in serverSocketStruct; /**< Structure of the server socket containing the port to listen to and the interface to bind. */
	int sockSize; /**< Size of the socket structure. */
	char* buffer; /**< Pointer to the buffer to store the incoming data. */
	pthread_t listenThread; /**< Thread to listen to the incoming connections. */
}VSC_STAT, *VSC_PSTAT;

//mcastMessaging.h
/**
 * Structure to pass to the multicast communication threads.
 **/
typedef struct MCM_threadStruct {
	int socket; /**< Socket number. */
	struct sockaddr_in address; /**< Address from where to listen - send messages. */
	PMSGBUFF buffer; /**< Buffer where to store/read the messages to be received/sent. */
}MCM_threadStruct;

/**
 * Structure to store all the information abount the multicast communications server. 
 **/
typedef struct MCM_serverStats{
	int state; /**< State of the multicast communications server. 0 - Stop, 1 - Active. */
	int generalSocket; /**< Socket of the general multicast group. */
	int stationSocket; /**< Socket of the station multicast group. */
	struct ip_mreq generalMreq; /**< Structure containing the general group information. */
	struct ip_mreq stationMreq; /**< Structure containing the station group information. */
	struct in_addr generalGroup; /**< Address of the general group. */
	struct in_addr stationGroup; /**< Address of the station group. */
	PMSGBUFF generalInbox; /**< Inbox for the messages coming from the general group. */
	PMSGBUFF stationOutbox; /**< Outbox for the messages to be sent to the station group. */
	pthread_t listenThread; /**< Threat to listen to the messages from the general group. */
	pthread_t sendThread; /**< Thread to send the messages to the station group. */
}MCM_serverStats;


#endif /* structures_h */
