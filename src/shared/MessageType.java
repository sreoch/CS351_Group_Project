package shared;

public enum MessageType {

//    Message types for Client -> Server
    LOGIN,
    CREATE_ACCOUNT,
    TRANSFER,
    DEPOSIT,
    WITHDRAW,
    VIEW_TRANSACTIONS,
    LOGOUT,

//    Message types for Server -> Clint
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    SUCCESS,
    FAILED,
    BALANCE_UPDATE,
    TRANSACTION_LIST

}
