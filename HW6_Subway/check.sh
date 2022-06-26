#!/bin/bash
echo "-compile start-"

# Compile
javac -encoding UTF8 Subway.java

mkdir -p my_output

start=$SECONDS

echo "-execute your program-"
for i in $(seq 1 60)
do
	# 무한루프를 방지하기 위해 input 당 시간제한 2초
        # Matching.java를 실행하는데, < input/$i.txt > 경로의 파일을 System.in으로 받아오고,
        # my_output/txt파일에 출력한다.
        # fileReader로 안해도 이런식으로 가능하다!
        # $(<txt)파일은 String[] args에 input으로 들어온다!
	timeout 1 java Subway $(<testset/argument/$i.txt) < testset/input/$i.txt > my_output/$i.txt
done

# testset의 실행에 소요된 시간
echo "Execution time : $((SECONDS-start)) seconds"

echo "-print wrong answers-"

for i in $(seq 1 60)
do
        #결과가 정답과 다를 경우 그 위치를 출력
        cmp my_output/$i.txt testset/output/$i.txt
        # diff my_output/$i.txt testset/output/$i.txt

done
tar -cvf HW6.tar Subway.java
