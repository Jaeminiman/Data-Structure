import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;


public class BigInteger {
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "Wrong Input";
    
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("");

    int[] operand;
    boolean is_negative = false;


    public BigInteger(int i) {
        String k = String.valueOf(i);
        int[] array = new int[k.length()];
        for (int j = 0; j < k.length(); ++j) {
            array[j] = k.charAt(j) - '0';
        }

        operand = array;
    }

    public BigInteger(int[] num1) {
        operand = num1;
    }

    public BigInteger(String s) {
        int[] array;
        if (s.charAt(0) == '-') {
            s = s.substring(1);
            array = new int[s.length()];
            for (int j = 0; j < s.length(); ++j) {
                array[j] = s.charAt(j) - '0';
            }
            is_negative = true;
        } else if (s.charAt(0) == '+') {
            s = s.substring(1);
            array = new int[s.length()];
            for (int j = 0; j < s.length(); ++j) {
                array[j] = s.charAt(j) - '0';
            }
        } else {
            array = new int[s.length()];
            for (int j = 0; j < s.length(); ++j) {
                array[j] = s.charAt(j) - '0';
            }

        }


        operand = array;
    }

    public BigInteger add(BigInteger big) {
        int[] left = this.operand;
        int[] right = big.operand;
        int leftLen = left.length;
        int rightLen = right.length;
        int len = Math.max(leftLen, rightLen);
        int minLen = Math.min(leftLen, rightLen);


        int[] result;

        int carry = 0;
        int temp = 0;



        result = new int[len];


        for (int i = 1; i <= minLen; ++i) {

            temp = (int) (left[leftLen - i] + right[rightLen - i] + carry);
            carry = 0;
            if (temp >= 10) {
                temp -= 10;
                carry = 1;
            }
            result[len - i] = temp;
        }


        if (len == leftLen) {
            for (int k = minLen + 1; k <= len; ++k) {
                temp = (int) (left[len - k] + carry);
                carry = 0;
                if (temp >= 10) {
                    temp -= 10;
                    carry = 1;
                }
                result[len - k] = temp;
            }
        }

        else {
            for (int k = minLen + 1; k <= len; ++k) {
                temp = (int) (right[len - k] + carry);
                carry = 0;
                if (temp >= 10) {
                    temp -= 10;
                    carry = 1;
                }
                result[len - k] = temp;
            }
        }

        if (carry == 1) {
            int[] moreArray = new int[len + 1];
            for (int i = len; i > 0; --i) {
                moreArray[i] = result[i - 1];
            }
            moreArray[0] = carry;
            result = new int[len + 1];
            result = moreArray;
        }


        return new BigInteger(result);
    }








    public BigInteger subtract (BigInteger big) {

        int[] left = this.operand;
        int[] right = big.operand;
        int leftLen = left.length;
        int rightLen = right.length;
        int len = Math.max(leftLen, rightLen);
        int minLen = Math.min(leftLen,rightLen);
        int[] bigger = new int[len];
        int[] smaller = new int[minLen];
        int[] result = new int[len];
        int count = 0;
        boolean swap = false;


       if(len==leftLen){
           if(minLen == len){
               for(int k = 0; k < len ; ++k){
                   if(left[k] > right[k]){
                       bigger = left;
                       smaller = right;
                       break;
                   }
                   else if(left[k] == right[k]){
                       count +=1;
                       continue;
                   }
                   else{
                       bigger = right;
                       smaller = left;
                       swap = true;
                       break;
                   }
               }

               if(count == len){
                   return new BigInteger(0);
               }
           }

           else{
               bigger = left;
               smaller = right;
           }

       }
       else {
           bigger = right;
           smaller = left;
           swap = true;
       }


        int temp;
        for (int i = 1; i <= minLen; ++i) {
            temp = (int) (bigger[len - i] - smaller[minLen - i]);
            if (temp < 0) {
                bigger[len - (i+1)] -= 1;
                temp += 10;
            }

            result[len-i] = temp;
        }
        for (int k = 0 ; k < len - minLen ; ++k) {
            result[k] = bigger[k];
        }


        int zeroCount = 0;
        for (int j = 0 ; j < len ; ++j){
            if(result[j] == 0){
                zeroCount +=1;
            }
            else{
                break;
            }
        }

        int[] lessArray = new int[len - zeroCount];

        for (int i = len - zeroCount - 1 ; i >= 0 ; --i) {
            lessArray[i] = result[i + zeroCount];
        }

        BigInteger arr = new BigInteger(lessArray);

        if(swap == true){
            arr.is_negative = true;
        }

        return arr;
    }

    public BigInteger multiply (BigInteger big)
    {
        int[] left = this.operand;
        int[] right = big.operand;
        int leftLen = left.length;
        int rightLen = right.length;
        int[] tmp = new int[rightLen+leftLen-1];

        if(left[0] == 0 || right[0] == 0){
            return new BigInteger(0);
        }

        for(int i = 0; i < rightLen ; ++i){
            for(int j = 0; j < leftLen ; ++j){
                tmp[i+j] += right[i] * left[j];
            }
        }

        for(int i = rightLen+leftLen-2 ; i >= 1 ; --i){
            while(tmp[i] >= 10){
                tmp[i] -= 10;
                tmp[i-1] += 1;
            }
        }
        if(tmp[0] >= 10){
            int[] moreArray = new int[rightLen+leftLen];

            while (tmp[0] >= 10){
                tmp[0] -= 10;
                moreArray[0] += 1;
            }
            for(int j = 1 ; j <= rightLen+leftLen-1; ++j){
                moreArray[j] = tmp[j-1];
            }
            tmp = new int[moreArray.length];
            tmp = moreArray;
        }


        return new BigInteger(tmp);
    }


    @Override
    public String toString ()
    {
        char[] chars = new char[this.operand.length];
        for(int j = 0; j < this.operand.length; j++) {
            chars[j] =(char) (this.operand[j] + '0');
        }
        return String.valueOf(chars);
    }

    static BigInteger evaluate (String input) throws IllegalArgumentException
    {
      input = input.replaceAll(" ", "");
      Pattern pattern = Pattern.compile("(\\W?\\d+)(\\W)(\\W?\\d+)");
      Matcher matcher = pattern.matcher(input);
      matcher.find();
      String arg1 = matcher.group(1);
      String arg2 = matcher.group(3);
      String operator = matcher.group(2);

      BigInteger num1 = new BigInteger(arg1);
      BigInteger num2 = new BigInteger(arg2);
      BigInteger val;

      if (num1.is_negative == true){
          if (num2.is_negative == true) {
              if (operator.equals("+")) {
                  val = num1.add(num2);
                  val.is_negative = true;
              } else if (operator.equals("-")) {
                  val = num2.subtract(num1);
              } else {
                  val = num1.multiply(num2);
              }
          }
          else{
              if (operator.equals("+")) {
                  val = num2.subtract(num1);
              } else if (operator.equals("-")) {
                  val = num1.add(num2);
                  val.is_negative = true;
              } else {
                  val = num1.multiply(num2);
                  val.is_negative = true;
              }
          }
      }
      else{
          if (num2.is_negative == true) {
              if (operator.equals("+")) {
                  val = num1.subtract(num2);
              } else if (operator.equals("-")) {
                  val = num1.add(num2);
              } else {
                  val = num1.multiply(num2);
                  val.is_negative = true;
              }
          }
          else{
              if (operator.equals("+")) {
                  val = num1.add(num2);
              } else if (operator.equals("-")) {
                  val = num1.subtract(num2);
              } else {
                  val = num1.multiply(num2);
              }
          }

      }



      return val;
    }



    public static void main (String[] args) throws Exception
    {
      try (InputStreamReader isr = new InputStreamReader(System.in)) {
          try (BufferedReader reader = new BufferedReader(isr)) {
              boolean done = false;
              while (!done) {
                  String input = reader.readLine();



                  try {
                      done = processInput(input);
                  } catch (IllegalArgumentException e) {
                      System.err.println(MSG_INVALID_INPUT);
                  }
              }
          }
      }
    }

    static boolean processInput (String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);

        if (quit) {
            return true;
        } else {
            BigInteger result = evaluate(input);
            if(result.is_negative == true){
                System.out.print('-');
                System.out.println(result.toString());
            }
            else{
                System.out.println(result.toString());
            }


            return false;
        }
    }

    static boolean isQuitCmd (String input)
    {

        return input.equalsIgnoreCase(QUIT_COMMAND);
    }

}