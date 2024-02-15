plugins {
  id("module-manager")
}

dependLibBase()
dependLibConfig()
dependLibUtils()

dependApiLogin()
dependApiAccount()

useARouter(false)

