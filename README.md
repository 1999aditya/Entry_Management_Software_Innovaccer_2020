# Visitor Tracker

## Demo Video
[Watch the video here.](https://youtu.be/sGesUCM-2Y8)

## Approach

Workflow of the software is like, when a **Visitor** enters the office, Guard seated at the entrance clicks on the `CHECK-IN FORM` and takes his/her details and details of the person whom they want to meet. 

 > When Guard presses `CHECK-IN` Button, following tasks occurs at the Backend

* __*Mobile Number*__ and __*Email ID*__ are checked using **JAVA REGX API**, Such that these details taken in the form of a __*String*__ follow the pattern specified.
\
 If not matched, then required to be corrected and `CHECK-IN` Button be pressed again.


* If matched then all the details with the *__current time stamp__ as the __check-in time__* are stored onto the __*Firebase RealTime Database*__ to track the visitor 
entered in the office.

* Simultaneously an *__Email__* using the __*Java Mail API*__ as well as a *__SMS__* 
 using the __*SmsManger API*__ is sent to the **Host**, informing them about the details of the **Visitor** coming to meet them.

After the meeting is over and **Visitor** starts to leave the office, Guard at the Exit Gate clicks on the `CHECK-OUT FORM` and asks for his/her __*Mobile Number*__ *(acts as a unique key)* and makes an Entry there.

> When Guard presses `CHECK-OUT` Button, following tasks occurs at the Backend

* __*Mobile Number*__  is again checked using **JAVA REGX API**. If not matched, then required to be corrected and `CHECK-OUT` Button be pressed again.

* If **Visitor** has *Checked-In*, then their details are fetched from the __*Firebase RealTime Database*__ and simultaneously **Visitor** can provide the checkout time. Otherwise they'll be asked to, first *Check-In*.

* Once this is done, an *__Email__* using the __*Java Mail API*__ is sent to the **Visitor**, mentioning details regarding their visit to the **Host's** office. 

   Simultaneously **Visitor's** details are deleted from Database.

### Firebase RealTime Database

Two nodes are created here, which are as follows :-

* **Current**

   - The Purpose of this node is to store the details of the **Visitors Currently 
      Present** in the office to avoid multiple entries of the same person at the 
      same time and it improves Data Accessing when needed.

   - Details are stored when the **Visitor** does *Check-In* and Entry of that 
     **Visitor** gets deleted once they do *Check-Out*.

   - **Key** for this node is **"Visitor’s Mobile Number"**.

* **Visitor**
 
  - The Purpose of this node is to record the details of the **Visitors**, for 
    throughout the Day .This can also be used as a *LogBook of Visitor's*.

   - Details are stored when **Visitor** does *Check-In* and Entry of that 
     **Visitor** gets updated with exit time once they do *Check-Out*. 
     
  - **Key** for this node is **"Visitor’s Mobile Number + Check-In Time"**. 

    > So,that If same person visit's again, then their new Entry with new Key will 
       be created and only that will be Updated Once they leave.
     \
     Past Entry, If any, will not be changed.

    Data in this node can be use to fetch details of visitors, who visited on that particular day. If needed to store data for more than a day or any longer period, we just need to modify the Key to do so. 
    
    

    **Details stored when a visitor does Check-In**
![CHECK-IN](https://user-images.githubusercontent.com/32017030/69657384-1d438f00-10a0-11ea-863f-408d50404a68.png)


      **Visitor node gets updated when that person do Check-Out**
![CHECK-OUT](https://user-images.githubusercontent.com/32017030/69657466-46fcb600-10a0-11ea-8912-f9cad7b02ee5.jpg)


### Project Structure
![Screenshot (70)](https://user-images.githubusercontent.com/32017030/69749263-22700f00-1170-11ea-8522-44b33211a12c.png)


### Technology Stack
* Java
* Android Studio 3.5
* XML
* Java Mail API
* SmsManager API

#### Hardware Supported 

- minSdkVersion 23
- targetSdkVersion 29
