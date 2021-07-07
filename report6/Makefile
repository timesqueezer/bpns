.PHONY: socket

socket:
	javac socket/SocketThrdServer.java socket/ClientWorker.java socket/Useradmin.java && CLASSPATH=$(pwd) java socket.SocketThrdServer

socket_client:
	javac socket/SocketClient.java && CLASSPATH=$(pwd) java socket.SocketClient

socket_useradmin:
	javac socket/SocketThrdServer.java socket/ClientWorker.java socket/Useradmin.java && CLASSPATH=$(pwd) java socket.Useradmin
