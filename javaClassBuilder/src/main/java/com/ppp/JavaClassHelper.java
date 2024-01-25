package com.ppp;

/**
 * @author Whoopsunix
 */
public class JavaClassHelper {
    /**
     * 继承 AbstractTranslet
     */
    private boolean extendsAbstractTranslet = false;
    /**
     * 本地 JavaClass 文件
     */
    private String javaClassFilePath;

    /**
     * JavaClassHelper 类型
     */
    private String JavaClassHelperType;
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
    private String HEADER = "X-Token";
    private String PARAM = "cmd";
    // Godzilla
    private String key = "3c6e0b8a9c15224a";
    public String pass = "pass";

    /**
     * JavaClass 信息
     */
    // javaClassName 是最外层的类名
    private String javaClassName;
    private boolean randomJavaClassName = true;
    private String javaClassPackageHost;

    // 内存马全限定类名
    // 这个目前的场景是 需要指定注入类名的时候，所以会将随机类名、自定义步骤提前
    private String CLASSNAME;

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
        return JavaClassHelperType;
    }

    public void setJavaClassHelperType(String javaClassHelperType) {
        JavaClassHelperType = javaClassHelperType;
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

    public String getCLASSNAME() {
        return CLASSNAME;
    }

    public void setCLASSNAME(String CLASSNAME) {
        this.CLASSNAME = CLASSNAME;
    }
}
