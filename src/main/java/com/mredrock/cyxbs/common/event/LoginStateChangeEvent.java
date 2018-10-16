package com.mredrock.cyxbs.common.event;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */

/**
 * Post ths event sticky when user login or logout
 */
public class LoginStateChangeEvent {

    private boolean newState;

    /**
     * Construct this event
     *
     * @param newState true = login, false = logout
     */
    public LoginStateChangeEvent(boolean newState) {
        this.newState = newState;
    }

    /**
     * Get the new state after user login or logout
     *
     * @return true = user has login, false = user has logout
     */
    public boolean getNewState() {
        return newState;
    }
}
