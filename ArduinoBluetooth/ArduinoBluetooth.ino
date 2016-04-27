#include <Servo.h>
#include <SerialCommand.h>
#include <Timer.h>

char DRIVE_PIN1 = 12;
char DRIVE_PIN2 = 11;
char DRIVE_ENABLE_PIN = 3;
char DEFAULT_STEER = 90;
char STEER_SERVO = 9;

Servo myservo;
SerialCommand sCmd;
Timer t;
bool pingReceived = false;

int readArgument(int defaultValue = 255){
  int argument = 0;
  char *arg;

  arg = sCmd.next();  
  if (arg != NULL) {
    return atoi(arg);
  }
  else{
    return defaultValue;
  }
}

void forward() {
  int speed = readArgument(255);  
  digitalWrite(DRIVE_PIN1, 1);
  digitalWrite(DRIVE_PIN2, 0);
  analogWrite(DRIVE_ENABLE_PIN, speed);
  Serial.println("forward");
  Serial.println(speed);
}

void backward() {
  int speed = readArgument(255);
  digitalWrite(DRIVE_PIN1, 0);
  digitalWrite(DRIVE_PIN2, 1);
  analogWrite(DRIVE_ENABLE_PIN, speed);
  Serial.println("backward");
  Serial.println(speed);
}

void reset() {
  digitalWrite(DRIVE_PIN1, 0);
  digitalWrite(DRIVE_PIN2, 0);
  myservo.write(DEFAULT_STEER);
  Serial.println("reset");  
}

void steer() {
  int angle = readArgument(90);
  myservo.write(angle);
  Serial.println("steer");
}

void ping(){
  Serial.println("@");
}

void release(){
  digitalWrite(DRIVE_PIN1, 0);
  digitalWrite(DRIVE_PIN2, 0);  
}

void unrecognized(const char *command) {
  Serial.println("What?");
}

void setup() {
  Serial.begin(115200);
  pinMode(DRIVE_PIN1, OUTPUT);
  pinMode(DRIVE_PIN2, OUTPUT);
  pinMode(DRIVE_ENABLE_PIN, OUTPUT);

  myservo.attach(STEER_SERVO);
  myservo.write(DEFAULT_STEER);

  Serial.println("Ready");

  sCmd.addCommand("F", forward);
  sCmd.addCommand("B", backward);
  sCmd.addCommand("S", steer);  
  sCmd.addCommand("P", release);
  sCmd.addCommand("R", reset);
  sCmd.addCommand("@", ping);  
  sCmd.setDefaultHandler(unrecognized);
}

void loop() {
  sCmd.readSerial();
}