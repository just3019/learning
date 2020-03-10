## spring

###spring 事务
顶级接口：PlatformTransactionManager提供三个方法
```java
public interface PlatformTransactionManager {
    TransactionStatus getTransaction(@Nullable TransactionDefinition var1) throws TransactionException;

    void commit(TransactionStatus var1) throws TransactionException;

    void rollback(TransactionStatus var1) throws TransactionException;
}
```

spring 事务传播机制？
事务的传播定义在TransactionDefinition接口中。
PROPAGETION_REQUIRED:支持当前事务，如果不存在，创建一个新的。
PROPAGETION_SUPPORTS:支持当前事务，如果不存在则以非事务方式执行。
PROPAGETION_MANDATORY：支持当前事务，如果不存在则抛错。
PROPAGETION_REQUIRED_NEW:创建一个新事务，如果当前存在事务则挂起。
PROPAGETION_NOT_SUPPORTED:不支持当前事务，而是始终以非事务方式执行。
PROPAGETION_NEVER：不支持当前事务，如果存在则抛错。
PROPAGETION_NESTED：嵌套事务，允许内部事务范围回滚。



##springboot

### springboot jar启动分析
1.在jar包中，有一个MANIFEST.MF文件，该文件中有Main-Class的特殊条目，执行java -jar就是来执行这个Main-Class指定的类。
```
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Built-By: root
Start-Class: org.demon.ApplicationLauncher
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Spring-Boot-Version: 2.2.2.RELEASE
Created-By: Apache Maven 3.6.1
Build-Jdk: 1.8.0_141
Main-Class: org.springframework.boot.loader.JarLauncher
```
由上图可以看到java -jar 执行的是org.springframework.boot.loader.JarLauncher 这个类。JarLauncher的main方法。
```
JarLauncher.java
public static void main(String[] args) throws Exception {
    (new JarLauncher()).launch(args);
}

Launcher.java
protected void launch(String[] args) throws Exception {
    JarFile.registerUrlProtocolHandler();
    ClassLoader classLoader = this.createClassLoader(this.getClassPathArchives());
    this.launch(args, this.getMainClass(), classLoader);
}

这里的重点是getMainClass()这个方法。
ExecutableArchiveLauncher
protected String getMainClass() throws Exception {
    Manifest manifest = this.archive.getManifest();
    String mainClass = null;
    if (manifest != null) {
        mainClass = manifest.getMainAttributes().getValue("Start-Class");
    }

    if (mainClass == null) {
        throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
    } else {
        return mainClass;
    }
}
```
上面的过程是查找Start-Class 类。找到具体的启动类后执行。
这个过程中，springboot已经帮我们定义好了classPath：BOOT-INF/classes/;BOOT-INF/lib/