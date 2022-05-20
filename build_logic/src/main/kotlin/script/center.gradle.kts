package script

apply(plugin="script.dependency-substitution")

if(!project.name.equals("module_app")){
    apply(plugin="script.publications")
}
