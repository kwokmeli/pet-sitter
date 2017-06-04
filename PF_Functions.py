import RPi.GPIO as GPIO
import time
import Adafruit_ADS1x15
import HX711
import pigpio
import Adafruit_PCA9685

FOOD_LC_DATA_PIN=4
FOOD_LC_CLOCK_PIN=17

FOOD_SERVO_PIN=0
FOOD_SERVO_PWM_DC=0

WATER_SERVO_PIN=0

WATER_LC_DATA_PIN=18
WATER_LC_CLOCK_PIN=24

WATER_CONTAINER_FULL = 0          # Loadcell reading when water container is FULL
WATER_BOWL_FULL = 0             # Loadcell reading when water bow is FULL


pwm = Adafruit_PCA9685.PCA9685()
print "pwm object created:"
pwm.set_pwm_freq(50)
print "pwm frequency set to 50 Hz"

def loadCell(food_Amount,data_Pin,clock_Pin):
    pi = pigpio.pi()
    if not pi.connected:
            exit(0)
    s = HX711.sensor(pi, DATA=data_Pin, CLOCK=clock_Pin, mode=1, callback=None)
    s.set_mode(1)
    s.start()
    #stop = time.time() + 20
    r = 0
    while r <= food_Amount:
        c, m, r = s.get_reading()
	print "LoadCell reading: ", r
        time.sleep(0.1)
    s.pause()
    s.cancel()
    pi.stop()
    return r


def dispense_Food(food_Amount):
    print "Tring to dispense: ", food_Amount
    #pwm = Adafruit_PCA9685.PCA9685()
    #pwm.set_pwm_freq(50)
    pwm.set_pwm(FOOD_SERVO_PIN, 0, FOOD_SERVO_PWM_DC)
    loadCell(food_Amount,FOOD_LC_DATA_PIN,FOOD_LC_CLOCK_PIN)
    pwm.set_pwm(FOOD_SERVO_PIN, 0, 0)    # Stop pwm wave
    return

'''
    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(servo_Pin,GPIO.OUT)
    pwm = GPIO.PWM(servo_Pin,50)
    pwm.start(7.6)
    loadCell(food_Amount, channel)        ### Need calibration
    pwm.stop()
    GPIO.cleanup()
    return
'''

def dispense_Water(water_Amount):
    # pwm_DC = 160 is 0 Degree
    # pwm_DC = 560 is 180 Degree

    #pwm = Adafruit_PCA9685.PCA9685()
    #pwm.set_pwm_freq(50)
    pwm.set_pwm(WATER_SERVO_PIN, 0, 560) # 90 Degree
    time.sleep(.5)
    pwm.set_pwm(WATER_SERVO_PIN, 0, 360) # 90 Degree
    time.sleep(.3)
    loadCell(water_Amount,FOOD_LC_DATA_PIN,FOOD_LC_CLOCK_PIN)
    pwm.set_pwm(WATER_SERVO_PIN, 0, 560) # 90 Degree
    time.sleep(.5)
    pwm.set_pwm(WATER_SERVO_PIN, 0, 0)   # Stop pwm wave
    return

'''
    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(servo_Pin,GPIO.OUT)
    pwm = GPIO.PWM(servo_Pin,50)
    pwm.start(SOME_DEGREE_POSITION)         ### Need calibration
    loadCell(food_Amount,FOOD_LC_DATA_PIN,FOOD_LC_CLOCK_PIN)
    pwm.start(SOME_DEGREE_POSITION)           ### Need calibration
    pwm.stop()
    GPIO.cleanup()
    return
'''


def dispense_Water():
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

def get_lc_reading(data_Pin,clock_Pin):
    pi = pigpio.pi()
    if not pi.connected:
            exit(0)
    s = HX711.sensor(pi, DATA=data_Pin, CLOCK=clock_Pin, mode=1, callback=None)
    s.set_mode(1)
    s.start()
    time.sleep(.7)
    c, m, r = s.get_reading()
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
