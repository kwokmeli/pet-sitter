import pigpio
import time
import HX711

#def cbf(count, mode, reading):
	#print(count, mode, reading)

pi = pigpio.pi()
if not pi.connected:
	exit(0)
lc1 = HX711.sensor(pi, DATA=17, CLOCK=4, mode=1)
lc1.set_mode(1)
lc2 = HX711.sensor(pi, DATA=27, CLOCK=18, mode=1)
lc2.set_mode(1)

lc1.start()
lc2.start()

time.sleep(2)

stop = time.time() + 30

lst1 = []
lst2 = []

while time.time() <= stop:
	time.sleep(0.1)
	c1, m1, r1 = lc1.get_reading()
	#time.sleep(0.05)
	c2, m2, r2 = lc2.get_reading()
	#time.sleep(0.1)
	if r1 != None:
		if len(lst1) == 0:
			max1 = r1
			min1 = r1
                        #print "r1: ", r1,
		if r1 >= max1:
			max1 = r1
		if r1 <= min1:
			min1 = r1
		lst1.append(r1)
		print "r1: ", r1,

        if r2 != None:
		if len(lst2) == 0:
			max2 = r2
			min2 = r2
                        #print "    r2: ", r2
		if r2 >= max2:
			max2 = r2
		if r2 <= min2:
			min2 = r2
		lst2.append(r2)
		print "    r2: ",r2
#	time.sleep(0.1)

print "r1 Average: ", sum(lst1)/len(lst1)
print "r1 Max: ", max1
print "r1 Min: ", min1
print "r1 len: ", len(lst1)

print "r2 Average: ", sum(lst2)/len(lst2)
print "r2 Max: ", max2
print "r2 Min: ", min2
print "r2 len: ", len(lst2)

lc1.pause()
lc1.cancel()

lc2.pause()
lc2.cancel()

pi.stop()

