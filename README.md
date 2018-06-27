# ComRaDe
A Tool For Evaluating Race Detectors. Currently, three state-of-the-art race detectors have been evaluated on it.  They are CalFuzzer, Rv-Predict and DATE. Results can be found [here]().

## 1. dataset
- A total of 53 programs from both real-world applications(6) and academic artifacts(42). Containing 558 data races recorded in the format of Rv-Predict(https://runtimeverification.com).

## 2. a tool--comparison platform.
- Run it at cn.edu.thu.platform.frame.MainFrame.java. You can see the video at https://youtu.be/TPNNx5Hotp0.
- Data race information is located in benchmark.xml.

## 3. How to use
ComRaDe can be used in accordance with the following steps: 
- 1) prepare the command script in advance, according to user specifications of the detector; 
- 2) run ComRaDe, read the XML file that stores data race information; 
- 3) enter the run interface, read the command script, and click the `run` button to run them; 
- 4) select a comparison tool from preset extraction patterns after finishing the running process; 
- 5) click the `compare` button to view evaluation result; 
- 6) select the best and most suitable race detector and perform the concurrent testing. 