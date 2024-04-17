package com.ppp;

import com.ppp.annotation.JavaClassType;

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
     * 内存马
     */
    // 组件
    private String middleware;
    // 内存马类型
    private String memShell;
    // 内存马功能
    private String memShellFunction;

    /**
     * 以下为内存马可自定义信息
     */
    private String NAME = "Whoopsunix";
    private String HEADER = "X-Token";
    private String PARAM = "cmd";
    private String PATH = "/whoopsunix";
    // Godzilla
    private String key = "3c6e0b8a9c15224a";
    public String pass = "pass";
    // 内存马约束请求头
    private String lockHeaderKey = "User-Agent";
    private String lockHeaderValue = "Whoopsunix";

    /**
     * JavaClass 信息
     */

    // 用于内存马时 代表真正注入请求上下文的类名，loader 不可控
    private String CLASSNAME;
    // 是否为注入器
    private boolean isLoader;
    // javaClassName 是最外层的类名
    private String javaClassName;
    private boolean randomJavaClassName;
    private String javaClassPackageHost;



    public JavaClassHelper() {
        this.randomJavaClassName = true;
        this.extendsAbstractTranslet = false;
        this.isLoader = false;
        this.javaClassType = JavaClassType.Default;

//        this.NAME = RanDomUtils.generateRandomOnlyString(4, 7);
//        this.HEADER = RanDomUtils.generateRandomOnlyString(4, 7);
//        this.PARAM = RanDomUtils.generateRandomOnlyString(4, 7);
//        this.PATH = "/" + RanDomUtils.generateRandomOnlyString(4, 7);
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

    public String getPARAM() {
        return PARAM;
    }

    public void setPARAM(String PARAM) {
        this.PARAM = PARAM;
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

    public String getJavaClassPackageHost() {
        return javaClassPackageHost;
    }

    public void setJavaClassPackageHost(String javaClassPackageHost) {
        this.javaClassPackageHost = javaClassPackageHost;
    }
}
