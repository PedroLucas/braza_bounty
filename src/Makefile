JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
	Agent.java \
	Cell.java \
	MapModel.java \
	Planner.java \
	Point.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
