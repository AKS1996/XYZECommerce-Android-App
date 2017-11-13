## XYZECommerce-Android-App

# Features:
Category I XYZECommerce Android App
Backend,in php, on IBM Bluemix
User functionalities: signing up, loging in, info update
Account functionalities: check balance, get past transcations, make transactions

# Flow of events while login/signup
User logins using FB/Google
A SLID(Social Login ID) is sent to server to check whether user exists or not
If existing user Return account info Redirect to Dashboard Activity
If new Redirect to signup activity Sent account info to server New Entry in MAIN_TABLE Account open with initial balance of $200

# Flow of events while transactions
User enters the SLID, Amount to be transferred
User Prompted for password
Data sent to server
New Entry in TRANS_TABLE created

# DB Structure
MAIN_TABLE SLID	Social Login ID	- UNIQUE UNAME	Account User name FNAME	User's First Name CELL	Cell Phone Number PWRD	Password for making transactions
TRANS_TABLE TO_SLID FROM_SLID AMOUNT
