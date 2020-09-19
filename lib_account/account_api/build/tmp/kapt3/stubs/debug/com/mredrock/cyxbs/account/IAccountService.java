package com.mredrock.cyxbs.account;

import java.lang.System;

/**
 * Created By jay68 on 2019-11-12.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\n"}, d2 = {"Lcom/mredrock/cyxbs/account/IAccountService;", "Lcom/alibaba/android/arouter/facade/template/IProvider;", "getUserEditorService", "Lcom/mredrock/cyxbs/account/IUserEditorService;", "getUserService", "Lcom/mredrock/cyxbs/account/IUserService;", "getUserTokenService", "Lcom/mredrock/cyxbs/account/IUserTokenService;", "getVerifyService", "Lcom/mredrock/cyxbs/account/IUserStateService;", "account_api_debug"})
public abstract interface IAccountService extends com.alibaba.android.arouter.facade.template.IProvider {
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mredrock.cyxbs.account.IUserService getUserService();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mredrock.cyxbs.account.IUserStateService getVerifyService();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mredrock.cyxbs.account.IUserEditorService getUserEditorService();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mredrock.cyxbs.account.IUserTokenService getUserTokenService();
}