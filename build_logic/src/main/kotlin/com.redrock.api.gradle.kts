val isSingleModuleDebug: String by project

if(isSingleModuleDebug.toBoolean()){
    apply(plugin="com.redrock.api-debug")
}else{
    apply(plugin = "com.redrock.api-release")
}


