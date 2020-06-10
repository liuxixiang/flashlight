
# ----------------------------- 通用配置 -----------------------------

#不混淆任何包含native方法的类的类名以及native方法名
-keepclasseswithmembernames class * {
    native <methods>;
}

# Parcelable 和 Serializable的子类不混淆
-keep class * implements android.os.Parcelable {*;}
-keepclassmembers class * implements java.io.Serializable {*;}

#不混淆枚举中的values()和valueOf()方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# R文件
-keepclassmembers class **.R$* {
    public static <fields>;
}

# ----------------------------- 项目自定义混淆配置 -----------------------------

# 实体类
-keep class **.bean.** {*;}


# ---------------------------------第三方包-------------------------------
-keepattributes *Annotation*

-keepclassmembers class ** {

@com.squareup.otto.Subscribe public *;

@com.squareup.otto.Produce public *;

}
