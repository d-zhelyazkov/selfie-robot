# selfie-robot

The first version of a robot that finds itself in a camera image. Built as my BSc project in Computer Science (Technical University of Varna); this is the work behind two papers:

- Zhelyazkov, D. (2017). *Plane transformation algorithm for a robot self-detection.* 2017 Computing Conference (IEEE). [ieeexplore.ieee.org/document/8252281](https://ieeexplore.ieee.org/document/8252281/)
- Karova, M., Zhelyazkov, D., et al. (2015). *Path Planning Algorithm for Mobile Robot.* ACS '15, WSEAS Press.

## How it works

An Android app (`SelfieRobot_Camera2/`) watches the robot through the phone camera (Camera2 API, manual exposure, ISO and focus), finds the robot's LEDs with OpenCV thresholding (Otsu and custom threshold finders), and sends movement commands to the robot over Bluetooth.

## Stack

Java · Android · OpenCV · Bluetooth

Its successor, running on a Raspberry Pi, is [selfie-robot-2](https://github.com/d-zhelyazkov/selfie-robot-2).
