# **Human PokeDex**

### A crime detection and campus management system that helps in the detection, classification and subsequent mitigation of crimes occurring in a region of surveillance.  

### Find the complete thesis [here](https://drive.google.com/file/d/1AOSW5C8Con3GtNauXkv9PnpVY6GhWrSh/view?usp=sharing).  

## **Overview**

**Human PokeDex** aims at promoting safety on campus by automating the task of monitoring and reporting crimes by assigning the responsibility of detecting criminal or abnormal activity to a system which is well-versed in **deducing patterns** that distinguish criminal activity from normal activity.
\
\
**In addition to detecting abnormality from footage**, the vast CCTV network intertwined with the campus management system can be used for further implementations:

- Detecting crimes in a footage fed from a camera and recognizing people involved in the crime
- Enabling a student tracker system (Since CCTVs can now recognize faces, the vast CCTV network can maintain timestamps on a student’s whereabouts at any given instant of time)
- A one-stop app which
  - leverages the same face recognition model from CCTVs to recognize criminals from a mobile phone
  - provides data from student tracker log in order to find the whereabouts of students/professors
  - retrieves relevant data on students (recognized by face or from a dropdown list)
  - receives alert notifications from the nearest CCTV camera witnessing a crime
  - stays in sync with the database linked with the CCTV network for better management of complaints


In order to fulfil these objectives, a greater objective was to generate video classification inferences from a normal 2D CNN, along with a minor objective of recognizing faces using simple vector-based classification algorithms.