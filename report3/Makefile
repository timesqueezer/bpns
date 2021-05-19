.PHONY: socket5_4

default:
	javac socket5_3/SocketThrdServer.java socket5_3/ClientWorker.java && CLASSPATH=$(pwd) java socket5_3.SocketThrdServer

socket5_4:
	javac socket5_4/SocketThrdServer.java socket5_4/ClientWorker.java socket5_4/Useradmin.java && CLASSPATH=$(pwd) java socket5_4.SocketThrdServer

socket5_4_client:
	javac socket5_4/SocketClient.java && CLASSPATH=$(pwd) java socket5_4.SocketClient

socket5_4_useradmin:
	javac socket5_4/SocketThrdServer.java socket5_4/ClientWorker.java socket5_4/Useradmin.java && CLASSPATH=$(pwd) java socket5_4.Useradmin
