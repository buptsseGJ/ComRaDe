The HTML is for the `Dataset Preparation` subsection in paper.

```
<reports>
    <report name="testRace1" totalTime="0.074">
        <race>
            <line1>41</line1>
            <line2>47</line2>
            <variable>x</variable>
            <packageClass>testRace.TestRace1</packageClass>
            <detail><![CDATA[
                    Race: <testRace.TestRace1$1: void run()>|$i0 = <testRace.TestRace1: int x>|41 - <testRace.TestRace1$2: void run()>|<testRace.TestRace1: int x> = $i1|47
                    Race: <testRace.TestRace1$1: void run()>|<testRace.TestRace1: int x> = $i1|41 - <testRace.TestRace1$2: void run()>|<testRace.TestRace1: int x> = $i1|47
                    Race: <testRace.TestRace1$2: void run()>|$i0 = <testRace.TestRace1: int x>|47 - <testRace.TestRace1$1: void run()>|<testRace.TestRace1: int x> = $i1|41
                    ]]></detail>
            <runningTime>;m;</runningTime>
            <variableType>PT</variableType>
            <codeStructure>bs-bs</codeStructure>
            <methodSpan>SS</methodSpan>
            <sensitiveBranch>no</sensitiveBranch>
            <sensitiveLoop>no</sensitiveLoop>
            <cause>NS</cause>
            <commonUsage>;+;</commonUsage>
            <bug>            
            </bug>
        </race>
        <race>
            ...
        </race>
    </report>
    ...
</reports>
```

`reports`: The root element representing the whole dataset. It contains 48 report element.

`report`: It is a single evaluation program with name and totalTime attributes indicating its name identifier and pure running time. One report consists of several data races marked as race element. 

`race`: This element represents a pair of data races. Each race element contains several other elements.

`line1`: One line number of a pair of data race

`line2`: The other line number of a pair of data race

`variable`: The variable name which data race occurs in.

`packageClass`: The package and class name which data race occurs in.

`detail`: Information about call stack causing data race.

`runningTime`: Temporarily not be used. For future expansion.

`variableType`: We referred to the Java data type, combined with data race, then divided the variable type into four categories: primitive types, reference types, collection types and mixed usage.

`codeStructure`: Taking into account the control ﬂow structure of Java program language, the following code structures are related to data race: basic statements, branch conditions, statements in branch, loop conditions and statements in loop.

`methodSpan`: We divide the race locations into three categories: the same method of the same class, different methods of the same class, different methods of different classes.

`sensitiveBranch`: It record whether this pair of data race is a schedule-sensitive branch.

`sensitiveLoop`: Temporarily not be used. For future expansion.

`cause`: In term of synchronization operations, we can divide them into three categories: no synchronization, partial synchronization and incorrect synchronization.

`commonUsage`: It contains some common and typical usage habits causing data race.

`bug`: Temporarily not be used. For future expansion.
