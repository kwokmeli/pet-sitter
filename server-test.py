import socket 
import sys

APP_IP_ADDR="69.91.185.87"

def new_Socket(i):
    message = "FOOD," + str(i)
    new_s.sendall(message + "\n")
    print message
#    new_s.close()
    return


HOST = '108.179.184.39'
#HOST = '173.250.183.115'
PORT = 9999

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.settimeout(2)

new_s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
new_s.connect((APP_IP_ADDR, 9999))


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
i = 100
while 1:
    print "hello"
    new_Socket(i)
    i-=1
    try:
	conn, addr = s.accept()
    	print 'Connect with ' + addr[0] + ':' + str(addr[1])
    	buf = conn.recv(64)
	print buf
    except socket.error as err:
	print err


s.close()
new_s.close()

    #print 'Connect with ' + addr[0] + ':' + str(addr[1])
    #buf = conn.recv(64)
    #print buf
    #s.close()
