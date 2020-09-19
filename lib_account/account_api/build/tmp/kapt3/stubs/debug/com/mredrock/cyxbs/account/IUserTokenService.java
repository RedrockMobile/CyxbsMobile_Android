package com.mredrock.cyxbs.account;

import java.lang.System;

/**
 * Created by yyfbe, Date on 2020-02-09.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&\u00a8\u0006\u0005"}, d2 = {"Lcom/mredrock/cyxbs/account/IUserTokenService;", "", "getRefreshToken", "", "getToken", "account_api_debug"})
public abstract interface IUserTokenService {
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getRefreshToken();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getToken();
}