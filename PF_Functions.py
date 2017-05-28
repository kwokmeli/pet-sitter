import RPi.GPIO as GPIO
import time
import Adafruit_ADS1x15
import HX711
import pigpio
import Adafruit_PCA9685

FOOD_LC_DATA_PIN=4
FOOD_LC_CLOCK_PIN=17


def loadCell(food_Amount,data_Pin,clock_Pin):
    pi = pigpio.pi()
    if not pi.connected:
            exit(0)
    s = HX711.sensor(pi, DATA=data_Pin, CLOCK=clock_Pin, mode=1, callback=None)
    s.set_mode(1)
    s.start()
    stop = time.time() + 20
    r = 0
    while r <= food_Amount:
        c, m, r = s.get_reading()
	print "LoadCell reading: ", r
        time.sleep(0.1)
    s.pause()
    s.cancel()
    pi.stop()
    return r


def dispense_Food(servo_Channel,pwm_DC,food_Amount):
    print "Tring to dispense: ", food_Amount 
    pwm = Adafruit_PCA9685.PCA9685()
    pwm.set_pwm_freq(50)
    pwm.set_pwm(servo_Channel, 0, pwm_DC)
    loadCell(food_Amount,FOOD_LC_DATA_PIN,FOOD_LC_CLOCK_PIN)
    pwm.set_pwm(servo_Channel, 0, 0)    # Stop pwm wave
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

def dispense_Water(servo_Pin, water_Amount):
    # pwm_DC = 160 is 0 Degree
    # pwm_DC = 560 is 180 Degree

    pwm = Adafruit_PCA9685.PCA9685()
    pwm.set_pwm_freq(50)
    pwm.set_pwm(servo_Pin, 0, 560) # 90 Degree
    time.sleep(.5)
    pwm.set_pwm(servo_Pin, 0, 360) # 90 Degree
    time.sleep(.3)
    loadCell(water_Amount,FOOD_LC_DATA_PIN,FOOD_LC_CLOCK_PIN)
    pwm.set_pwm(servo_Pin, 0, 560) # 90 Degree
    time.sleep(.5)
    pwm.set_pwm(servo_Pin, 0, 0)   # Stop pwm wave
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
