import socket
import sys
import PF_Functions as PF

HOST = '192.168.0.67'
PORT = 9999

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
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
    conn, addr = s.accept()
    print 'Connect with ' + addr[0] + ':' + str(addr[1])
    buf = conn.recv(64)
    print buf
    if buf == "empty":
        PF.check_Water()

    if buf == "dispense food":
        PF.dispense_Food(amount)
    if buf == "dispense water"
        PF.dispense_Water(amount):
    

s.close()
