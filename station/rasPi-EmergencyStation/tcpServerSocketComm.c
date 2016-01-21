/** @file tcpServerSocketComm.h Server connection related function definitions */

#include "tcpServerSocketComm.h"

SSC_STAT serverSocketStat; /**< Structure containing all the parameters for the connection to the server and it's state. */
PMSGBUFF SSC_serverSendBuffer; /**< Outbox for the messages to be sent to the server. */

/**
 * Initialize the connection to the server.
 * Initializes the sockets and resolves the DNS name of the server to connect to. 
 * It then calls the other functions to continue the initialization.
 **/
void SSC_initServerConnection() {
	int waitTime = 1;
	serverSocketStat.state = 1;
	serverSocketStat.buffer = calloc(SSC_SRV_BUFLEN, sizeof(char));
	serverSocketStat.sockSize = sizeof(struct sockaddr_in);
	serverSocketStat.serverInfo = NULL;
	serverSocketStat.serverSocket = socket(AF_INET, SOCK_STREAM, 0);
	if (serverSocketStat.serverSocket < 0) {
		perror("Error creating socket");
	}
	while (!serverSocketStat.serverInfo) {
		printf("Trying to search server IP address....\n");
		serverSocketStat.serverInfo = gethostbyname(SSC_SRV_ADDRESS);
		if (serverSocketStat.serverInfo == NULL) {
			perror("Host unreachable\n");
			sleep(waitTime);
			waitTime <<= 1;
		}
	}

	serverSocketStat.serverSocketStruct.sin_family = AF_INET;
	serverSocketStat.serverSocketStruct.sin_len = sizeof(struct sockaddr_in);
	memcpy(&serverSocketStat.serverSocketStruct.sin_addr, serverSocketStat.serverInfo->h_addr_list[0], serverSocketStat.serverInfo->h_length);
	serverSocketStat.serverSocketStruct.sin_port = htons(SSC_SRV_PORT);
	printf("Server IP Address: %s\n", inet_ntoa(serverSocketStat.serverSocketStruct.sin_addr));
	SSC_initBuffers();
	SSC_makeServerConnection();
	SSC_initServerConnThreads();
	
}

/**
 * Initialize the buffers involved in the server communication.
 **/
void SSC_initBuffers() {
	SSC_serverSendBuffer = MB_initBuffer(10);
	
	
}

/**
 * Start the threads for listening and sending messages to the server.
 **/
void SSC_initServerConnThreads() {
	pthread_create(&serverSocketStat.listenThread, NULL, SSC_msgListenerThreadFunc, NULL);
	pthread_create(&serverSocketStat.sendThread, NULL, SSC_msgSenderThreadFunc, NULL);
}

/**
 * Shutdown the connection to the server. 
 * It tryes to shutdown and close the socket gracefully. It then wait to the threads to exit by sending them a stop condition.
 **/
void SSC_stopServerConn() {
	serverSocketStat.state = 0;
	shutdown(serverSocketStat.serverSocket, SHUT_RDWR);
	close(serverSocketStat.serverSocket);
	pthread_join(serverSocketStat.listenThread, NULL);
	pthread_join(serverSocketStat.sendThread, NULL);
}

/**
 * Create the connection to the server.
 * Tryes to connect to the server. If the connection fails. Waits for a time period wthich grows exponentially.
 **/
void SSC_makeServerConnection() {
	int waitTime = 1;
	while (connect(serverSocketStat.serverSocket, (struct sockaddr *)&serverSocketStat.serverSocketStruct, serverSocketStat.sockSize) < 0) {
		perror("Error connecting to server");
		sleep(waitTime);
		waitTime <<= 1;
		close(serverSocketStat.serverSocket); //Advanced Programming in the Unix Enviorement, page 607, for testing in OSX, as after failed conexions, in *nix the socket is in an undefined state.
		serverSocketStat.serverSocket = socket(AF_INET, SOCK_STREAM, 0);
		if (serverSocketStat.serverSocket < 0) {
			perror("Error creating socket");
		}
	}
	printf("Server Connected\n");
}

/**
 * Send a message to the server. It directly sends a message to the server by reading it from a message structure.
 * @param msg The message to send.
 **/
void SSC_sendMessageToServer(PMESSAGE msg) {
	send(serverSocketStat.serverSocket, msg->fullMsg, strlen(msg->fullMsg), 0);
}

/**
 * Listen to the messages coming to the server. 
 8 It listens to new messages, storing them to a message structure if they are valid. Then it puts them in the parser inbox.
 **/
void SSC_listenToServerMsg() {
	
	int msgLength = 0;
	PMESSAGE msg;
	MP_PRECEIVERSTR receiver = calloc(1, sizeof(MP_RECEIVERSTR));
	receiver->clientBuff = serverSocketStat.buffer;
	receiver->bufferLength = SSC_SRV_BUFLEN;
	receiver->maxMsgLength = SSC_RCV_MAXLEN;
	while (((msgLength = recv(serverSocketStat.serverSocket, serverSocketStat.buffer,SSC_RCV_MAXLEN , 0)) > 0) && serverSocketStat.state == 1) {
		printf("Mensaje recibido de srv: %s, msgLength: %d\n", receiver->clientBuff, msgLength);
		receiver->msgLength = msgLength;
		msg = MP_messageReceiver(receiver);
		if (msg != NULL) {
			msg->source = 0;
			MB_putMessage(receivedMsgBuff, msg);
		}
	}
}
/**
 * Thread to send the messages stored in the outbox to the server.
 * It waits for messages to be put in the server's outbox and sends these when they are available.
 **/
void* SSC_msgSenderThreadFunc(void* args) {
	PMESSAGE msg;
	while (serverSocketStat.state == 1) {
		msg = MB_getMessage(SSC_serverSendBuffer);
		SSC_sendMessageToServer(msg);
		printf("%s sent to server\n", msg->fullMsg);
		MP_wipeMessage(msg);
	}
	pthread_exit(NULL);
}
/**
 * Thread to listen to messages coming from the server. 
 * It waits for new messages to arrive. If the connection has failed, tryes to reconnect to the server.
 **/
void* SSC_msgListenerThreadFunc(void* args) {
	while (serverSocketStat.state == 1) {
		SSC_listenToServerMsg();
		if (serverSocketStat.state == 1) {
			SSC_makeServerConnection();
		}
		
	}
	pthread_exit(NULL);
}