import RPi.GPIO as GPIO
import time

servo_Pin = 11
GPIO.setmode(GPIO.BOARD)
GPIO.setup(servo_Pin,GPIO.OUT)
pwm = GPIO.PWM(servo_Pin,50)
pwm.start(7.6)
time.sleep(5)
pwm.stop()
GPIO.cleanup()
