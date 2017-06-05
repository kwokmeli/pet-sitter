import socket
import sys

#HOST = '172.20.10.9'
HOST = '173.250.183.115'
PORT = 9999

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.settimeout(2)
# socket.socket: create socket
# socket.AF_INET: address format, internet = ip addresses
# socket.SOCK_STREAM: two-way connection-based byte streams

print 'Socket created.'
# bind socket to host and port

try:
    s.bind((HOST, PORT))
except socket.error as err:
    print 'Bind failed, error code: ' + str(err[0]) + ' , Message: ' + err[1]
    sys.exit()

print 'Socket bind successful.'

# listen(): sets up and starts TCP listener
s.listen(5)
print 'Socket is now listening.'

while 1:
    print "hello"

    try:
	conn, addr = s.accept()
    	print 'Connect with ' + addr[0] + ':' + str(addr[1])
    	buf = conn.recv(64)
	print buf
    except socket.error as err:
	print err


s.close()

    #print 'Connect with ' + addr[0] + ':' + str(addr[1])
    #buf = conn.recv(64)
    #print buf
    #s.close()
