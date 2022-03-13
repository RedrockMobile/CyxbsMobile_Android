val isSingleModuleDebug: String by project

if(isSingleModuleDebug.toBoolean()){
    apply(plugin="com.redrock.module.debug")
}else{
    apply(plugin = "com.redrock.module.release")
}

