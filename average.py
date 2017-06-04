import pigpio
import time
import HX711

#def cbf(count, mode, reading):
	#print(count, mode, reading)

pi = pigpio.pi()
if not pi.connected:
	exit(0)
s = HX711.sensor(pi, DATA=17, CLOCK=4, mode=1)

s.set_mode(1)

s.start()
time.sleep(0.7)
stop = time.time() + 300
lst = []
while time.time() <= stop:
	c, m, r = s.get_reading()
	if r != None:
		if len(lst) == 0:
			max = r
			min = r
		if r >= max:
			max = r
		if r <= min:
			min = r
		lst.append(r)
		print r
	time.sleep(0.1)

print "Average: ", sum(lst)/len(lst)
print "Max: ", max
print "Min: ", min
s.pause()
s.cancel()

pi.stop()

