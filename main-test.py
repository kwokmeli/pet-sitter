import socket
import sys
import PF_Functions as PF
import time

SCHEDULE_ON = 0

#HOST = '172.20.10.9'
HOST = '173.250.183.115'
PORT = 9999

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.settimeout(0.5)
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

    if SCHEDULE_ON:
	print "Schedule is on"
	now = time.strftime('%H:%M').split(":")
	hour = int(now[0])
	min = int(now[1])
#	print "current time is :", now
#	print "scheduled time is ", start_hour, start_min
	if hour == start_hour and min == start_min:
	    print "Scheduled dispensing"
#	    PF.dispense_Food(amount)
	    start_hour += interval_hour
	    if start_hour >= 24:
	        start_hour %= 24
	    start_min += interval_min
	    print "Finished dispensing"
	    print "next feeding time is ", start_hour, ":", start_min
    try:
        conn, addr = s.accept()
        print 'Connect with ' + addr[0] + ':' + str(addr[1])
        buf = conn.recv(64)
        print buf
        command = buf.split(",")
        if len(command) == 2:
            if command[0] == "FOOD":
	        amount = int(command[1])
	        print "Dispensing ", amount, "g of Food"
	        PF.dispense_Food(amount)
            if command[0] == "WATER":
	        delay = int(command[1])
	        print "Dispensing water to full in ", delay, " seconds"
		time.sleep(delay)
#	        PF.dispense_Water()
        elif len(command) == 6:
	    schedule = command[1].split(":")
	    start_hour = int(schedule[0])
	    start_min = int(schedule[1])
	    freq = int(command[3])
	    amount = int(command[5])
	    SCHEDULE_ON = 1
	    interval_hour = 24/freq
	    interval_min = 24%freq
	    if interval_min != 0:
    	        interval_min = float(interval_min)/float(freq) * 60
	    print "Scheduled to dispense ", amount, "g of food ", freq, " times a day starting ", start_hour, ":", start_min
	
    except socket.error as err:
	print "nothing happens"
	


s.close()

    #print 'Connect with ' + addr[0] + ':' + str(addr[1])
    #buf = conn.recv(64)
    #print buf
    #s.close()
