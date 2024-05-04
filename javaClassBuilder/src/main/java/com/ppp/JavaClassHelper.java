package com.ppp;

import com.ppp.annotation.JavaClassEnhance;
import com.ppp.annotation.JavaClassMakerEnhance;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.MemShellType;

/**
 * @author Whoopsunix
 */
public class JavaClassHelper {
    /**
     * 继承 AbstractTranslet
     */
    private boolean extendsAbstractTranslet;
    /**
     * 本地 JavaClass 文件
     */
    private String javaClassFilePath;

    /**
     * JavaClassHelper 类型
     */
    private String javaClassHelperType;
    /**
     * 额外生成方式
     */
    private String javaClassType;
    /**
     * 增强输出
     */
    private JavaClassEnhance[] javaClassEnhances;
    /**
     * 创建增强
     */
    private JavaClassMakerEnhance[] javaClassMakerEnhances;

    /**
     * 内存马
     */
    // 组件
    private String middleware;
    // 内存马类型
    private String memShell;
    // 内存马功能
    private String memShellFunction;
    private String memShellType;

    /**
     * 以下为内存马可自定义信息
     */
    private String NAME;
    private String HEADER;
    private String RHEADER;
    //    private String PARAM = "cmd";
    private String PATH;
    // Godzilla
    private String key;
    public String pass;
    // 内存马约束请求头
    private String lockHeaderKey;
    private String lockHeaderValue;

    /**
     * JavaClass 信息
     */

    private String LoaderClassName;
    // 用于内存马时 代表真正注入请求上下文的类名，loader 不可控
    private String CLASSNAME;
    // 是否为注入器
    private boolean isLoader;
    // javaClassName 是最外层的类名
    private String javaClassName;
    private boolean randomJavaClassName;
    // 包名
    private String javaClassPackageName;


    public JavaClassHelper() {
        this.randomJavaClassName = true;
        this.extendsAbstractTranslet = false;
        this.isLoader = false;
        this.javaClassType = JavaClassType.Default;
        this.javaClassEnhances = new JavaClassEnhance[]{JavaClassEnhance.Default};
        this.javaClassMakerEnhances = new JavaClassMakerEnhance[]{JavaClassMakerEnhance.Default};
        this.memShellType = MemShellType.Default;
    }

    /**
     * Filed
     */
    public boolean isExtendsAbstractTranslet() {
        return extendsAbstractTranslet;
    }

    public void setExtendsAbstractTranslet(boolean extendsAbstractTranslet) {
        this.extendsAbstractTranslet = extendsAbstractTranslet;
    }

    public String getJavaClassFilePath() {
        return javaClassFilePath;
    }

    public void setJavaClassFilePath(String javaClassFilePath) {
        this.javaClassFilePath = javaClassFilePath;
    }

    public String getJavaClassHelperType() {
        return javaClassHelperType;
    }

    public void setJavaClassHelperType(String javaClassHelperType) {
        this.javaClassHelperType = javaClassHelperType;
    }

    public String getJavaClassType() {
        return javaClassType;
    }

    public void setJavaClassType(String javaClassType) {
        this.javaClassType = javaClassType;
    }

    public JavaClassEnhance[] getJavaClassEnhances() {
        return javaClassEnhances;
    }

    public void setJavaClassEnhances(JavaClassEnhance[] javaClassEnhances) {
        this.javaClassEnhances = javaClassEnhances;
    }

    public JavaClassMakerEnhance[] getJavaClassMakerEnhances() {
        return javaClassMakerEnhances;
    }

    public void setJavaClassMakerEnhances(JavaClassMakerEnhance[] javaClassMakerEnhances) {
        this.javaClassMakerEnhances = javaClassMakerEnhances;
    }

    public String getMiddleware() {
        return middleware;
    }

    public void setMiddleware(String middleware) {
        this.middleware = middleware;
    }

    public String getMemShell() {
        return memShell;
    }

    public void setMemShell(String memShell) {
        this.memShell = memShell;
    }

    public String getMemShellFunction() {
        return memShellFunction;
    }

    public void setMemShellFunction(String memShellFunction) {
        this.memShellFunction = memShellFunction;
    }

    public String getMemShellType() {
        return memShellType;
    }

    public void setMemShellType(String memShellType) {
        this.memShellType = memShellType;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getHEADER() {
        return HEADER;
    }

    public void setHEADER(String HEADER) {
        this.HEADER = HEADER;
    }

    public String getRHEADER() {
        return RHEADER;
    }

    public void setRHEADER(String RHEADER) {
        this.RHEADER = RHEADER;
    }

    public String getPATH() {
        return PATH;
    }

    public void setPATH(String PATH) {
        this.PATH = PATH;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getLockHeaderKey() {
        return lockHeaderKey;
    }

    public void setLockHeaderKey(String lockHeaderKey) {
        this.lockHeaderKey = lockHeaderKey;
    }

    public String getLockHeaderValue() {
        return lockHeaderValue;
    }

    public void setLockHeaderValue(String lockHeaderValue) {
        this.lockHeaderValue = lockHeaderValue;
    }

    public String getLoaderClassName() {
        return LoaderClassName;
    }

    public void setLoaderClassName(String loaderClassName) {
        LoaderClassName = loaderClassName;
    }

    public String getCLASSNAME() {
        return CLASSNAME;
    }

    public void setCLASSNAME(String CLASSNAME) {
        this.CLASSNAME = CLASSNAME;
    }

    public boolean isLoader() {
        return isLoader;
    }

    public void setLoader(boolean loader) {
        isLoader = loader;
    }

    public String getJavaClassName() {
        return javaClassName;
    }

    public void setJavaClassName(String javaClassName) {
        this.javaClassName = javaClassName;
    }

    public boolean isRandomJavaClassName() {
        return randomJavaClassName;
    }

    public void setRandomJavaClassName(boolean randomJavaClassName) {
        this.randomJavaClassName = randomJavaClassName;
    }

    public String getJavaClassPackageName() {
        return javaClassPackageName;
    }

    public void setJavaClassPackageName(String javaClassPackageName) {
        this.javaClassPackageName = javaClassPackageName;
    }
}
