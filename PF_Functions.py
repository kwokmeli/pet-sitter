import RPi.GPIO as GPIO
import time
import Adafruit_ADS1x15
import HX711
import pigpio
import Adafruit_PCA9685
import statistics

FOOD_LC_DATA_PIN=6
FOOD_LC_CLOCK_PIN=5

FOOD_SERVO_PIN=1
FOOD_SERVO_PWM_DC=320

FOOD_AGI_SERVO_PIN=2
FOOD_AGI_SERVO_PWM_DC=400

WATER_SERVO_PIN=0

WATER_LC_DATA_PIN=4
WATER_LC_CLOCK_PIN=17

WATER_CONTAINER_FULL = 0          # Loadcell reading when water container is FULL
WATER_CONTAINER_EMPTY = 276848
WATER_BOWL_FULL = 250766             # Loadcell reading when water bow is FULL

FOOD_BOWL_EMPTY = -75618
FOOD_BOWL_FULL = -27180 - FOOD_BOWL_EMPTY

pwm = Adafruit_PCA9685.PCA9685()
print "pwm object created:"
pwm.set_pwm_freq(50)
print "pwm frequency set to 50 Hz"

def loadCell_Food(cur_food,food_amount,data_Pin,clock_Pin):
    print "in loadCell_Food()"
    pi = pigpio.pi()
    if not pi.connected:
        exit(0)
    s = HX711.sensor(pi, DATA=data_Pin, CLOCK=clock_Pin, mode=1, callback=None)
    s.set_mode(1)
    s.start()
    time.sleep(0.7)
    if cur_food <= 0:
	"Food bowl is currently empty"
        threshold = food_amount
    else:
	"Food bowl contains", cur_food, "amount of food, adding", food__amount, "more."
	threshold = cur_food + food_amount
    print "cur_food", cur_food
    print "food_amount", food_amount
    print "threshold: ", threshold
    buff = [None]*30
    i = 0
    while r <= threshold:
        c, m, r = s.get_reading()
	if r != None:
	    r -= FOOD_BOWL_EMPTY		
 	count = 0
	while r == None:
            c, m, r = s.get_reading()
	    r -=FOOD_BOWL_EMPTY
	    time.sleep(0.1)
	    count+=1
	    if count >= 20:
		print "restart load cell"
		s.pause()
	        s.cancel()
		s = HX711.sensor(pi, DATA=data_Pin, CLOCK=clock_Pin, mode=1, callback=None)
		s.set_mode(1)
    		s.start()
    		time.sleep(0.7)
		c, m, r = s.get_reading()
		count = 0	

	print "LoadCell reading: ", r
	buff[i]=r
	i+=1
	if i == 30:
	    stdev = statistics.stdev(buff)
	    print "statistics.stdev:", statistics.stdev(buff)
	    i = 0
	    if stdev <= 50.0:
		fix_Jam()

        time.sleep(0.1)

    s.pause()
    s.cancel()
    pi.stop()
    return 

def fix_Jam():
    print "Jam"
    pwm.set_pwm(FOOD_SERVO_PIN,0,380)
    time.sleep(1)
    pwm.set_pwm(FOOD_SERVO_PIN,0,270)
    time.sleep(1.5)
    pwm.set_pwm(FOOD_SERVO_PIN,0,FOOD_SERVO_PWM_DC)
    time.sleep(.5)
    return


def loadCell_Water(data_Pin,clock_Pin):
    pi = pigpio.pi()
    if not pi.connected:
            exit(0)
    s = HX711.sensor(pi, DATA=data_Pin, CLOCK=clock_Pin, mode=1, callback=None)
    s.set_mode(1)
    s.start()
    time.sleep(0.7)
    r = 0
    while r <= WATER_BOWL_FULL:
        c, m, r = s.get_reading()
        print "LoadCell reading: ", r
        time.sleep(0.1)
    s.pause()
    s.cancel()
    pi.stop()
    return r


def dispense_Food(food_Amount):
    print "Tring to dispense: ", food_Amount
    cur_food = get_lc_reading(FOOD_LC_DATA_PIN,FOOD_LC_CLOCK_PIN)
    cur_food -= FOOD_BOWL_EMPTY
    food_Amount = convert_LC(food_Amount) 
    food_Amount -= FOOD_BOWL_EMPTY
    if cur_food >= FOOD_BOWL_FULL:
    	print "Already Full"
	return 
    if cur_food + food_Amount >= FOOD_BOWL_FULL:
	print "Dispensing more that full, will dispense to full instead"
	food_Amount = FOOD_BOWL_FULL - cur_food

    pwm.set_pwm(FOOD_AGI_SERVO_PIN, 0, FOOD_AGI_SERVO_PWM_DC)
    time.sleep(0.5)
    pwm.set_pwm(FOOD_SERVO_PIN, 0, FOOD_SERVO_PWM_DC)
    time.sleep(0.5)
    loadCell_Food(cur_food,food_Amount,FOOD_LC_DATA_PIN,FOOD_LC_CLOCK_PIN)
    pwm.set_pwm(FOOD_SERVO_PIN, 0, 0)    # Stop pwm wave
    time.sleep(0.2)
    pwm.set_pwm(FOOD_AGI_SERVO_PIN, 0, 0)    # Stop pwm wave
    time.sleep(0.2)
    return



def dispense_Water():
    # pwm_DC = 160 is 0 Degree
    # pwm_DC = 560 is 180 Degree

    cur_water = get_lc_reading(WATER_LC_DATA_PIN,WATER_LC_CLOCK_PIN)
    if cur_water >= WATER_BOWL_FULL:
	print "Already full"
	return

    pwm.set_pwm(WATER_SERVO_PIN, 0, 540) # 0 Degree
    time.sleep(.5)
    pwm.set_pwm(WATER_SERVO_PIN, 0, 270) # 90 Degree
    time.sleep(.3)
    loadCell_Water(WATER_LC_DATA_PIN,WATER_LC_CLOCK_PIN)
    pwm.set_pwm(WATER_SERVO_PIN, 0, 540) # 0 Degree
    time.sleep(.5)
    pwm.set_pwm(WATER_SERVO_PIN, 0, 0)   # Stop pwm wave
    time.sleep(.5)
    return

'''
def dispense_water():
    pwm.set_pwm(WATER_SERVO_PIN, 0, 560) # 0 Degree
    time.sleep(.5) #initialize spigot position
    while water_level() < 100:
        pwm.set_pwm(WATER_SERVO_PIN, 0, 360) # 90 Degree
        time.sleep(.3) #give servo time to reach 90 degree position
    print "Water bowl is refilled to full"
    pwm.set_pwm(WATER_SERVO_PIN, 0, 560) # Turn spigot back to off
    time.sleep(.3)
    pwm.set_pwm(WATER_SERVO_PIN, 0, 0)   # Stop pwm wave
    return
'''

def get_lc_reading(data_Pin,clock_Pin):
    pi = pigpio.pi()
    if not pi.connected:
            exit(0)
    s = HX711.sensor(pi, DATA=data_Pin, CLOCK=clock_Pin, mode=1, callback=None)
    s.set_mode(1)
    s.start()
    time.sleep(0.7)
    c, m, r = s.get_reading()
    while r == None:
	c, m, r = s.get_reading()
	time.sleep(0.1)
    print "LoadCell reading: ", r
    s.pause()
    s.cancel()
    pi.stop()
    return r

def water_level():
    reading = get_lc_reading(WATER_LC_DATA_PIN,WATER_LC_CLOCK_PIN)
    water_level = (reading/WATER_FULL)*100
    if water_level >= 100:
        return 100
    else:
        return water_level

def check_Water():
    if water_level() < 75:
        time.sleep(60)      # delay 1 min
        dispense_Water()
        return
    else:
        return

def low_Water_Warning():
    if water_level() < 10:
	print "WARNING: Refill water in the container"
    return 

def low_Food_Warning():
    return

def convert_LC(gram):
    print gram, " in LC reading is", gram*222.94 - 75648
    return gram*222.94 - 75648

def convert_Gram(lc_reading):
    print lc_reading, " in gram is", (lc_reading + 75648) / 222.94
    return (lc_reading + 75648) / 222.94

def stop_Servos():
    pwm.set_pwm(FOOD_AGI_SERVO_PIN, 0, 0)
    time.sleep(0.5)
    pwm.set_pwm(FOOD_SERVO_PIN, 0, 0)
    time.sleep(0.5)
