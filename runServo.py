import RPi.GPIO as GPIO
import time
import Adafruit_ADS1x15
import HX711
import pigpio
import Adafruit_PCA9685
import sys


pwm = Adafruit_PCA9685.PCA9685()

pwm.set_pwm_freq(50)
pwm.set_pwm(int(sys.argv[1]), 0, int(sys.argv[2]))
