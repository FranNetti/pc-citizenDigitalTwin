# Citizen Digital Twin
[Project for Pervasive Computing exam]

In the field of Digital Twins, this project realize a Personal Agent of a citizen that enables him to communicate with his own Digital Twin.
This project is related to the platform shown in [Citizen-DT repository](https://github.com/sbricco-house/citizen-dt).

## Requirements
- [JaCa-Android library](https://github.com/pslabunibo/jaca-android)
- Android 7.1 (limitation by current version of JaCa-Android)

## Directories structure
- **citizenPersonalAgentApp**: project related to the Personal Agent of a citizen. It's a M.A.S. realized in JaCa-Android
- **bracelet**: Arduino application for a smart devices that can detect a person's SpO2, heart rate and body temperature. It uses a Bluetooth device, a TMP36 and MAX30102 sensor
- **stakeholderApp**: project related to stakeholders that are interested in observing citizens' information. Current roles supported are medic and cop 