# HW1 - BigInteger

ToDo : Class 뼈대만 주어지고, 내부 구현은 스스로 하였다.

매우 큰 정수에 대해서도 제한없이 연산을 할 수 있도록 기본 자료형을 쓰지 않고 임의의 크기를 갖는 수를 char 배열(또는 byte 배열)로 받아 +, -, * 연산을 수행하는 프로그램을 구현합니다. 가장 기본이 되는 자료구조인 배열의 개념을 이해하고 활용하는 것이 과제의 목적입니다.

## 주의한 사항
1. Long등의 자료형을 사용할 수 없고, Int자료형의 정수 표현 범위를 넘어선 수에 대한 연산을 수행했어야 했기 때문에 각 정수를 한 자리 단위로 쪼개 연산을 수행하였다. 이 과정에서 Carry out을 고려한 연산을 설계했어야 했다.

기본 자료구조 : 배열

# HW2 - MovieDatabase

ToDo : MovieDB.java MyLinkedList.java

Linked List를 사용해서 영화의 장르와 제목이 저장되는 데이터베이스를 구현합니다. 이 데이터베이스에서는 삽입, 삭제, 검색 연산이 가능해야하며, 각 항목이 영화의 장르와 제목에 따라 정렬된 순서로 저장되어야 합니다. Linked List를 이해하는 것이 과제의 목적입니다.

기본 자료구조 : LinkedList로 Genre와 Movie(name)을 관리하고, 각 장르마다 영화 제목에 따라 정렬된 리스트로 데이터베이스를 구축.

# HW3 - CalculatorTest

ToDo : line 32 ~ 569

사칙연산을 컴퓨터가 이해할 수 있는 구조로 수행하기 위해 infix -> postfix 형태로 바꾼 후 연산을 수행.

## 주의한 사항
1. 연산자 간 우선순위 고려
2. divide 0 같은 에러처리

기본 자료구조 : Stack

# HW4 - SortingTest
ToDo : line 92 ~ 321

기본 정렬 알고리즘 구현과 실제 Input Size에 따른 시간 복잡도를 계산함으로써 결과 분석 수행.

# HW5 - Matching
ToDo : line 1 ~ 466

dataCommand : txt파일을 input으로 받아와 hashtable로 database 형성 (일종의 bag of words)
indexCommand : @(index)을 input으로 하여 해당 hash값을 갖는 모든 노드의 item 출력(preorder)
patternCommnad : ?(pattern string)을 input으로 하여 해당 String이 있는 위치(행, 열)을 전부 출력

## 주의한 사항
1. 자료구조를 Generic type으로 구현

기본 자료구조 : Hashtable의 item을 AVLTree로, AVLTree의 Node의 Item을 LinkedList로 하고 LinkedList이 필요한 이유는 동일한 6자리 word가 중복 위치할 수 있어서 그 위치 정보를 모두 담기 위함이다.

# HW6 - Subway

1. input으로 지하철 역 Node와 역 간 Edge 정보를 바탕으로 데이터 베이스 만들기
2. 출발역과 도착역 입력시 데이터 베이스를 바탕으로 최단 경로 산출 알고리즘 (Dijkstra Algorithm 사용)

Subway.java 코드 전부 스스로 작성

사용한 자료구조 : Hashtable, LinkedList, ArrayList
