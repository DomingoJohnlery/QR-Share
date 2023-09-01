# QRCode-Builder
An android QR Code builder and scanner. Turns text, files, images, videos into QR Code.
With firebase storage the files is stored anonymously, allowing the app to turn it into a shareable url. Thus, storing it into a QR Code.

# Connect Your Own Firebase Project?
1. In Android Studio Go To Tools > Firebase
2. Go To Authentication > Authenticate Using a Custom Authentication System
![image](https://github.com/DomingoJohnlery/QR-Share/assets/124936918/26ee7402-4e67-41ab-b370-eb03a0bfd79d)
3. Connect Your App To Firebase & Add The Firebase Authentication SDK To Your App
   - Here you will create a firebase account & project.
![Screenshot 2023-09-01 152423](https://github.com/DomingoJohnlery/QR-Share/assets/124936918/54a5024c-f426-44f1-96fc-3c87cacd4e12)
4. In Your Firebase Project Select Authentication > Sign-In Method Add Anonymous
![Screenshot 2023-09-01 152810](https://github.com/DomingoJohnlery/QR-Share/assets/124936918/9e84367f-4cd7-4955-bdb5-b9773ff827e2)
5. Set Firebase Cloud Storage & Create a Firebase Storage In Your Firebase Project
![Screenshot 2023-09-01 153356](https://github.com/DomingoJohnlery/QR-Share/assets/124936918/6dc2c12c-ffd9-41eb-8824-6a18967f190c)

Firebase Authentication & Cloud Storage is all you need.
![Screenshot 2023-09-01 153939](https://github.com/DomingoJohnlery/QR-Share/assets/124936918/8c4eab7c-53de-4072-87b7-834f71ee9035)
