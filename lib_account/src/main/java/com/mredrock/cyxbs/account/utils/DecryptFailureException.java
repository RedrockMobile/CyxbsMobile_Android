package com.mredrock.cyxbs.account.utils;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */

public class DecryptFailureException extends Exception {

    public DecryptFailureException() {
    }

    public DecryptFailureException(String message) {
        super(message);
    }

    public DecryptFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecryptFailureException(Throwable cause) {
        super(cause);
    }

}
