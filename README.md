# Smartphone_LSPR

Android app for interfacing with the smartphone-based fiber optic LSPR sensor designed by the Peng group at Dalian University of Technology, China(https://www.dlut.edu.cn). There are 24 bits per pixel in color images, which include three 8-bit integers (0−255) that indicate the intensity of RGB colors. The G values of all the pixels in the region of interest (ROI) are averaged as the intensity of green channel. After capturing the required images with 640 × 480 resolution, the average intensities of green channel were calculated and displayed on the screen in real time. A rectangular area covering the bright image of optical fiber’s end face was chosen as region of interest (ROI). Note: The coordinates of the ROI area are related to the connection between the sensor and the mobile phone. Here, the source code does not specify the specific coordinates, and the intensity of entire image is directly calculated. The time interval is 200 ms, and we can average the 10 values to reduce the noise level.

# Source Code Directories

- MainActivity.java: Application startup animation.
- ImageSPR.java： Detection activity.
- CameraUtil.java: Call the phone camera to calculate the green channel intensity.
- ChartUtil.java： Display the results.
- SaveUtil.java: Save results to SD card.
