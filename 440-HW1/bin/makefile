JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        GrepProcess.java \
        LoadManager.java \
        Main.java \
        MigratableProcess.java \
        MigratableProcessWrapper.java \
        ProcessChild.java \
        ProcessManager.java \
        ProcessRunner.java \
        Queue.java \
        ServerSocketWrapper.java \
        ShittyProcess.java \
        SlaveListener.java \
        SocketWrapper.java \
        TransactionalFileInputStream.java \
        TransactionalFileOutputStream.java \
        WebCrawler.java \
        Zip.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class