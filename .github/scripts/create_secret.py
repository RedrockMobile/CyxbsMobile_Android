import sys

file = open("secret.gradle", 'w')

file.write("def secret = [:]\n\n"
           "// === START ===\n\n"
           "// ===== in Code =====\n\n"
           "def buildConfigField = [:]\n"
           "buildConfigField.BUGLY_APP_ID = " + "\"\\\"" + sys.argv[1] + "\\\"\"\n"
           "buildConfigField.SOPHIX_APP_ID = " + "\"\\\"" + sys.argv[2] + "\\\"\"\n"
           "buildConfigField.SOPHIX_APP_SECRET = " + "\"\\\"" + sys.argv[3] + "\\\"\"\n"
           "buildConfigField.SOPHIX_RSA = " + "\"\\\"" + sys.argv[4] + "\\\"\"\n"
           "buildConfigField.UM_APP_KEY = " + "\"\\\"" + sys.argv[5] + "\\\"\"\n"
           "buildConfigField.UM_PUSH_SECRET = " + "\"\\\"" + sys.argv[6] + "\\\"\"\n"
           "buildConfigField.UM_SHARE_SINA_APP_KEY = " + "\"\\\"" + sys.argv[7] + "\\\"\"\n"
           "buildConfigField.UM_SHARE_SINA_APP_SECRET = " + "\"\\\"" + sys.argv[8] + "\\\"\"\n"
           "buildConfigField.UM_SHARE_QQ_ZONE_APP_ID = " + "\"\\\"" + sys.argv[9] + "\\\"\"\n"
           "buildConfigField.UM_SHARE_QQ_ZONE_APP_SECRET = " + "\"\\\"" + sys.argv[10] +"\\\"\"\n"
           "secret.buildConfigField = buildConfigField\n\n"
           "// ===== in Code end =====\n\n// ===== in Manifest =====\n\n"
           "def manifestPlaceholders = [:]\n"
           "manifestPlaceholders.amap_apikey = " +"\"\\\"" + sys.argv[11] +"\\\"\"\n"
           "secret.manifestPlaceholders = manifestPlaceholders\n"
           "// ===== sign key end =====\n\n// ===== sign key =====\n\ndef sign = [:]\n"
           "sign.RELEASE_KEY_ALIAS = " + "\""+sys.argv[12] +"\"\n"
           "sign.RELEASE_KEY_PASSWORD = " + "\""+sys.argv[13] +"\"\n"
           "sign.RELEASE_STORE_PASSWORD = " + "\""+sys.argv[14] +"\"\n"
           "secret.sign = sign\n\n// ===== sign key end =====\n\n// === END ===\next.secret = secret\n")
