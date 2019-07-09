# socket-multicam
Preview and record your own and a remote Android camera

My first Android app. Created for university (Mobile Applications course)

Features:
- use the app either as server or client
- preview your own camera
- preview a remote Android camera simultaneously (connected over Internet, local network or WiFi Direct)
- record both cameras (when server initiates recording, the video recording will take place on each device locally)
- generate a EDL cut list from tapping on the camera previews (similiar to Multicam editing) for later use in video editing software like Vegas Pro

Technologies used:
- Sockets
- Camera2
- MediaRecorder
- ImageReader
- WiFiP2pManager

The biggest thing I learned from doing this project is that Camera2 is a pain in the ass to work with and unnecessary complicated even for simple uses like previewing the camera.
