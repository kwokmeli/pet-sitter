import pigpio
import time
import HX711

#def cbf(count, mode, reading):
	#print(count, mode, reading)

pi = pigpio.pi()
if not pi.connected:
	exit(0)
s = HX711.sensor(pi, DATA=4, CLOCK=17, mode=1)

s.set_mode(1)

s.start()

stop = time.time() + 20
while time.time() <= stop:
	c, m, r = s.get_reading()
	print r
	time.sleep(0.5)

s.pause()
s.cancel()

pi.stop()
