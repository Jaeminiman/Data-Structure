import java.io.*;
import java.util.Stack;


public class CalculatorTest
{

	static StringBuilder sb = new StringBuilder();

	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine().trim();
				if (input.compareTo("q") == 0)
					break;

				command(input);
			}
			catch (Exception e)
			{
				System.out.println("ERROR");
				//System.out.println(e.toString());
			}
		}
	}

	private static void command(String input) throws Exception{
		// ToDo : infix to postfix
		// 숫자 연속 두번 나왔는지 error 체크
		twice_num_checking(input);

		String s = input.replaceAll("\t", "");
		s = s.replaceAll(" ","");


		sb.append("(");
		sb.append(s);
		sb.append(")");
		String infix = sb.toString();
		sb.setLength(0);

		String postfix = postfixing(infix);

		// postfix calculation
		String result = calculating(postfix);
		System.out.println(postfix);
		System.out.println(result);
	}

	private static String calculating(String postfix) throws Exception{
		// TODO
		Stack<Long> digit_Box = new Stack<>();
		long prev;
		long next;
		String[] arr = postfix.split(" ");

		for(String ch : arr){

			if(ch.equals("^")){
				next = digit_Box.pop();
				prev = digit_Box.pop();
				if(prev == 0 & next < 0){
					throw new Exception("PowerZeroException");
				}

				digit_Box.push((long) Math.pow(prev,next));
			}
			else if(ch.equals("*")){

				next = digit_Box.pop();
				prev = digit_Box.pop();

				digit_Box.push((long) (prev * next));
			}
			else if(ch.equals("/")){
				next = digit_Box.pop();
				prev = digit_Box.pop();

				if(next == 0){
					throw new Exception("DivideZeroException");
				}

				digit_Box.push((long) (prev / next));
			}
			else if(ch.equals("%")){
				next = digit_Box.pop();
				prev = digit_Box.pop();

				if(next == 0){
					throw new Exception("DivideZeroException");
				}
				digit_Box.push((long) (prev % next));
			}
			else if(ch.equals("+")){

				next = digit_Box.pop();
				prev = digit_Box.pop();

				digit_Box.push((long) (prev + next));
			}
			else if(ch.equals("-")){

				next = digit_Box.pop();
				prev = digit_Box.pop();

				digit_Box.push((long) (prev - next));
			}
			else if(ch.equals("~")){
				prev = digit_Box.pop();

				digit_Box.push((long) -prev);
			}
			else{
			digit_Box.push(Long.parseLong(ch));
			}
		}

		String result = String.valueOf(digit_Box.pop());
		return result;
	}

	private static String postfixing(String infix) throws Exception{
		Stack<String> group_correct;
		Stack<String> process_reverse;
		String postfix;

		//error check
		error_detecting(infix);

		group_correct = grouping(infix);

		while(true) {
			process_reverse = process(group_correct);
			if(process_reverse.peek().equals("End")){
				process_reverse.pop();
				postfix = process_reverse.pop();
				break;
			}else{
				group_correct = reversing(process_reverse);
				continue;
			}
		}
		return postfix;
	}

	private static Stack<String> grouping(String input) throws Exception{
		Stack<String> result_correct = new Stack<>();
		// correct는 정순서

		char ch;
		boolean paren;
		boolean power;
		boolean second_op;
		String preCh;

		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			paren = ch == ')' | ch == '(';

			// -는 unary도 있으므로 따로 처리해준다.
			// operation, parentheses인 경우
			if (not_minus_op(ch) | paren) {
				result_correct.push(String.valueOf(ch));
			} else if (ch == '-') {
				if (result_correct.isEmpty()) {
					result_correct.push("unary-");
				} else {
					preCh = result_correct.peek();
					if (preCh.equals("(") | preCh.equals("*") | preCh.equals("+") | preCh.equals("unary-") | preCh.equals("binary-") | preCh.equals("/") | preCh.equals("%")) {
						result_correct.push("unary-");

					} else {
						result_correct.push("binary-");
					}

				}
			}
			// char이 숫자인 경우
			else if(is_digit(ch)){
				sb.append(ch);
				if (is_digit(input.charAt(i+1))) {
					continue;
				} else {
					result_correct.push(sb.toString());
					sb.setLength(0);
				}
			}
			else{
				throw new Exception("1");
			}

		}


		return result_correct;
	}
	// correct가 들어왔을 때 괄호 1차 처리
	private static Stack<String> process(Stack<String> correct) throws Exception{
		Stack<String> result_reverse = new Stack<>();
		Stack<String> in_reverse = new Stack<>();
		Stack<String> tmp = new Stack<>();


		//group화된 Stack을 pop하니까 역순으로 뽑힘
		if(correct.size()  == 1){
			 result_reverse.push(correct.pop());
			 result_reverse.push("End");
		}
		else {

			while (!correct.isEmpty()) {
				String ch = correct.pop();

				if (ch.equals(")")) {
					loop:
					while (true) {
						String ch_in = correct.pop();
						if (ch_in.equals(")")) {
							result_reverse.push(")");

							while(!in_reverse.isEmpty()) {
								tmp.push(in_reverse.pop());
							}
							while(!tmp.isEmpty()) {
								result_reverse.push(tmp.pop());
							}
							continue;
						} else if (ch_in.equals("(")) {
							break loop;
						} else {
							in_reverse.push(ch_in);
							continue;
						}
					}
					// 최내부 괄호 내용을 역순 st_in stack형태로 저장
					// st_in에 대해 postfix 처리 수행
					result_reverse.push(relocateOp(in_reverse));
				}
				else {
					result_reverse.push(ch);
				}
			}
		}
		return result_reverse;
	}

	// 괄호처리 한 것을 다시 넣을 수 있게끔 재정리하는 작업 + 더 할게남았는지 체크
	//private static Stack<String> reloading_process(Stack<String> reverse){
	//	Stack<String> result_correct = new Stack<>();
	//	String check_more = "E";
	//	String ch;
	//
	//	while(reverse.isEmpty()) {
	//		ch = reverse.pop();
	//		if(ch == "("){
	//			check_more = "G";
	//		}
	//		result_correct.push(ch);
	//	}
	//	if(check_more == "G"){
	//		result_correct.push(check_more);
	//	}
	//
	//	return result_correct;
	//}

	//Stack 뒤집어 엎기
	private static Stack<String> reversing(Stack<String> reverse){
		Stack<String> result_correct = new Stack<>();
		while(!reverse.isEmpty()) {
			result_correct.push(reverse.pop());
		}
		return result_correct;
	}

	// 역순 st에 대해 적용하는 것이지..
	private static String relocateOp(Stack<String> st_reverse) throws Exception{
		Stack<String> result_correct;
		Stack<String> result_reverse;


		result_correct = thirdOp(reversing(secondOp(reversing(firstOp_uni(reversing(firstOp(st_reverse)))))));
		result_reverse = reversing(result_correct);
		while (!result_reverse.isEmpty()){
			sb.append(result_reverse.pop());
		}

		String result = sb.toString();
		sb.setLength(0);
		return result;

	}

	// 첫번째 우선순위 operation 처리
	// 연속된 연산자 Error 처리
	private static Stack<String> firstOp(Stack<String> st) throws Exception{
		Stack<String> result_correct = new Stack<>();
		// correct 는 정순서를 말한다
		Stack<String> power_Box = new Stack<>();
		Stack<String> unary_Box = new Stack<>();
		String grouped_Power;
		String ch;

		while(!st.isEmpty()) {
			ch = st.pop();

			if (ch.equals("^")) {
				String prev = result_correct.pop();
				String next = st.pop();


				if (st.isEmpty()) {
					grouped_Power = relocate_Power(prev, next, power_Box);
					result_correct.push(grouped_Power);
				}
				else {
					//^순서가 좀 특이한 점 반영
					if (st.peek().equals("^")) {
						sb.append(prev);
						sb.append(" ");
						sb.append(next);
						result_correct.push(sb.toString());
						sb.setLength(0);
						power_Box.push("^");
						continue;
					}
					else {
						grouped_Power = relocate_Power(prev, next, power_Box);
						result_correct.push(grouped_Power);
					}
				}

			}
			else{
				result_correct.push(ch);
			}
		}
		return result_correct;
	}

	private static Stack<String> firstOp_uni(Stack<String> st){
		Stack<String> result_correct = new Stack<>();
		// correct 는 정순서를 말한다
		Stack<String> power_Box = new Stack<>();
		Stack<String> unary_Box = new Stack<>();
		String ch;

		while(!st.isEmpty()) {
			ch = st.pop();
			if (ch.equals("unary-")) {
				String next = st.peek();
				if (next.equals("unary-")) {
					unary_Box.push("~");
				} else {
					sb.append(st.pop());
					sb.append(" ");
					sb.append("~");

					while (!unary_Box.isEmpty()) {
						sb.append(" ");
						sb.append(unary_Box.pop());
					}
					result_correct.push(sb.toString());
					sb.setLength(0);
				}
			} else {
				result_correct.push(ch);
			}
		}

		return result_correct;

		}

	// 두번째 우선순위 operation 처리
	// 정순서 처리함 (빠져 나오는 것은 역순)
	private static Stack<String> secondOp(Stack<String> st_reverse) throws Exception{
		Stack<String> result_correct = new Stack<>();
		// reverse 는 역순서를 말한다

		String ch;

		while(!st_reverse.isEmpty()){
			ch = st_reverse.pop();

			// 0으로 나누는 에러 처리 해야함(실제 계산에서 하면 됨)
			if(ch.equals("/") | ch.equals("%") | ch.equals("*")){
				String prev = result_correct.pop();
				String next = st_reverse.pop();
				//if(next.equals("0")){
				//	throw new Exception("divide zero exception");
				//}
				sb.append(prev);
				sb.append(" ");
				sb.append(next);
				sb.append(" ");
				sb.append(ch);
				result_correct.push(sb.toString());
				sb.setLength(0);
			}
			else {
				result_correct.push(ch);
			}
		}
		return result_correct;
	}

	private static Stack<String> thirdOp(Stack<String> st){
		Stack<String> result_correct = new Stack<>();
		// correct 는 정순서를 말한다

		String ch;

		while(!st.isEmpty()){
			ch = st.pop();

			if(ch.equals("+")){
				String prev = result_correct.pop();
				String next = st.pop();
				sb.append(prev);
				sb.append(" ");
				sb.append(next);
				sb.append(" ");
				sb.append(ch);
				result_correct.push(sb.toString());
				sb.setLength(0);
			}
			else if(ch.equals("binary-")){
				String prev = result_correct.pop();
				String next = st.pop();
				sb.append(prev);
				sb.append(" ");
				sb.append(next);
				sb.append(" ");
				sb.append("-");
				result_correct.push(sb.toString());
				sb.setLength(0);
			}
			else{
				result_correct.push(ch);
			}
		}
		return result_correct;
	}
	private static String relocate_Power(String prev, String next, Stack<String> operationBox){
		sb.append(prev);
		sb.append(" ");
		sb.append(next);
		sb.append(" ");
		while (!operationBox.isEmpty()) {
			sb.append(operationBox.pop());
			sb.append(" ");
		}
		sb.append("^");
		String result = sb.toString();
		sb.setLength(0);
		return result;
	}

	private static void error_detecting(String input) throws Exception{

		String no_paren_input = checking_paren(input);

		char[] arr = no_paren_input.toCharArray();

		if(not_minus_op(arr[0]) | !is_digit(arr[no_paren_input.length()-1])){
			throw new Exception("2");
		}

		for(int i =1 ; i < no_paren_input.length() - 1; i++){
			// arr[0]와 arr[last]에 대해서는 따로 고려해줘야함
			char prev = arr[i-1];
			char curr = arr[i];
			char next = arr[i+1];


			// 1. 중간이 not_minus_op인 경우
			if(not_minus_op(curr)){
				if(not_minus_op(next)){
					throw new Exception("3");
				}
				else if(!is_digit(prev)){
					throw new Exception("4");
				}
				else{
					continue;
				}

			}
			else if(curr == '-'){
				if(not_minus_op(next)){
					throw new Exception("5");
				}
				else{
					continue;
				}
			}
			else if(is_digit(curr)){
				continue;
			}
			// 모르는 문자 들어옴
			else{
				throw new Exception("6");
			}

		}


	}

	private static boolean is_digit(char ch){
		if((byte) ch >= 0x30 & (byte) ch <= 0x39){
			return true;
		}
		else{
			return false;
		}
	}
	private static boolean not_minus_op(char ch){
		boolean cond = ch == ('+')|ch == ('*') | ch == ('/') | ch == ('%')|ch =='^';
		return cond;
	}

	private static String checking_paren(String s) throws Exception{
		String no_outer_paren = s.substring(1,s.length()-1);
		char[] arr = no_outer_paren.toCharArray();
		int l_bound = 0;
		int r_bound = 0;

		for(char ch : arr){
			if(ch == '('){
				l_bound++;
			}
			else if(ch == ')'){
				r_bound++;
			}
			if(r_bound > l_bound){
				throw new Exception("7");
			}
		}
		if(r_bound!=l_bound){
			throw new Exception("8");
		}
		String result = s.replaceAll("\\)" , "");
		result = result.replaceAll("\\(", "");

		return result;
	}

	// '\t' 제거된 input "s"를 집어넣어서 숫자 두번나오는거 체크
	private static void	twice_num_checking(String s) throws Exception{

		String[] arr = s.split("[\\s|\\\t]+");
		for(int i = 0; i < arr.length - 1; i++){
			if(is_digit(arr[i].charAt(arr[i].length()-1)) & is_digit(arr[i+1].charAt(0))){
				throw new Exception("9");
			}
			else{
				continue;
			}
		}
	}

}
