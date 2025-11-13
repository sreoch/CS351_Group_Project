# API Docs

For the requests sent over a socket, the messageType determines what is to be done with the request and the parameters provide the information the client/server needs to action that request.

## Request Types Client -> Server

- LOGIN
- CREATE_ACCOUNT
- TRANSFER
- DEPOSIT
- WITHDRAW
- VIEW_TRANSACTIONS
- LOGOUT

### Example Requests

- LOGIN
  - Example Request: 
    - Message(MessageType.LOGIN, "scott:passwordhere")
  - Example Responses: 
    - Message(MessageType.LOGIN_SUCCESS, "Login Successfull")
    - Message(MessageType.LOGIN_FAILED, "Login failed. Invalid Credentials")
    - Message(MessageType.LOGIN_FAILED, "Message payload not in correct form.");
- CREATE_ACCOUNT
  - Example Requst: 
    - Message(MessageType.CREATE_ACCOUNT, "scott:passwordhere")
  - Example Responses:
    - Message(MessageType.SUCCESS, "Account successfully created");
    - Message(MessageType.FAILED, "Could not create account. User may already exist.")
- TRANSFER
  - Example Request:
    - Message(MessageType.TRANSFER, "100:recipientUserName")
- DEPOSIT
  - Example Request:
    - Message(MessageType.DEPOSIT, "100:AccountThatBelongsToUser")
  - Example Response:
    - Message(MessageType.SUCCESS, "Successfully deposited")
    - Message(MessageType.FAILED, "Amount must be greater than £0")
    - Message(MessageType.FAILED, "Amount must be a double.")
- WITHDRAW
  - Requst: Message(MessageType.WITHDRAW, "100":AccountThatBelongsToUser)
- VIEW_TRANSACTIONS
  - Example Requst:
    - Message(MessageType.VIEW_TRANSACTIONS)
  - Example Response:
    - Message(MessageType.SUCCESS, "")
- LOGOUT
  - Requst: Message(MessageType.LOGOUT)

## Request Types Server -> client

- LOGIN_SUCCESS
- LOGIN_FAILED
- SUCCESS
- FAILED
- BALANCE_UPDATE
- TRANSACTION_LIST
- CONNECTION_TIMEOUT

### Example Requests

- LOGIN_SUCCESS
  - Message(MessageType.LOGIN_SUCCESS)
- LOGIN_FAILED
  - Message(MessageType.LOGIN_FAILED, "Login Failed Invalid Credentials")
- SUCCESS
  - Message(MessageType.SUCCESS, "Transfer of £100 to recipientUserName successful")
- FAILED
  - Message(MessageType.FAILED, "Transfer of £100 to recipientUserName failed. User recipientUserName not found.")
- BALANCE_UPDATE
  - Message(MessageType.BALANCE_UPDATE, "UserAccount2:2000")
- TRANSACTION_LIST
  - Message(MessageType.TRANSACTION_LIST, "{transactions: [{'from': 'user1', 'to': 'user2', 'amount': 100}, {'from': 'user3', to: 'user1', amount: 50}]}")
- CONNECTION_TIMEOUT
  - Message(MessageType.CONNECITON_TIMEOUT)