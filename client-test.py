import socket
import sys

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('172.20.10.10', 9999))
#s.connect(('69.91.132.33', 9999))
while 1:
    message = raw_input('Enter your message: ')
    print ('sending "%s"' % message)
    s.sendall(message + "\n")

    #data = s.recv(16)
    #print ('received "%s"' % data)

s.close()
