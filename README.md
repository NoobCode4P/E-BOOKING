# HCMUS Computer Networking Project - Socket Programming - E-BOOKING

# 1. Project contents
In this project, our job is to write a server and a client application to provide an application for hotel room booking  using `Socket Programming` with TCP protocol at **Transport Layer**.
- Server manages booking information received from the users
- Hotel information, room reservation information is stored at server.
- Client is provided with multiple services to look up and book a room.

# 2. Services
## a. Account Registration
- Client registers an account including 
    - ***username*** (at least 5 characters: a-z, 0-9)
    - ***password*** (at least 3 characters)
    - **payment card number** (10 characters: 0-9)
- Server checks whether the account is valid or not
    - If valid, stores account information to database, sends a message of successful registration back to client
    - Otherwise, sends failed registration message to client

## b. Sign in
- Client should send ***username*** and ***password***

- Server checks account
    - If valid, sends **Sign In Successfully** message to client and a list of provided services to choose from.
    - If invalid, sends a message informing "Failed to Sign In" to client

## c. Search
- Client sends request consisting of ***the name of the hotel***, ***arrival date*** and ***departure date***.

- Server receives request from client and sends a list of unbooked rooms of that hotel back to client including **room type**, **descriptions**, **room price**.

## d. Room Reservation
- Client sends room booking information (***hotel name***, ***room type***, ***arrival and departure date***, notes)
- Server receives client's request, stores information to database and calculates the total price, informs to the client.


## e. Booking Cancellation
- Client is allowed to cancel room reservation within 24h starting from the moment of successful booking.

- Server checks the conditions to cancel:
    - If valid, cancels the reservation, deletes booking information from the database
    - Otherwise, informs to the client that the cancellation is unsuccessful.

# 3. IDE and Supporting Frameworks
- IDE: ***IntelliJ IDEA Community Edition 2022.1.3***
- Database management: **Microsoft SQL Server Management
Studio 18**
- Frameworks:
    - **Java Swing**: design GUI
    - **JDBC**: help to connect to the database

# 4. Teammates
- Nguyen Quang Binh, Luu Tan Phat and me