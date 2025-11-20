package heheheha;

import java.util.*;

public class ShuntingYard
{

    // Operator precedence map
    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
    static
    {
        PRECEDENCE.put("+", 1);
        PRECEDENCE.put("-", 1);
        PRECEDENCE.put("*", 2);
        PRECEDENCE.put("/", 2);
        PRECEDENCE.put("^", 3);  // exponent
    }

    public static void main(String[] args)
    {
        String[] examples =
        	{
            "(3 + 4) * 2 / (1 - 5)",
            "2 ^ 3 ^ 2",
            "(5 * (6 + 2))",
            "((2 + 3) * (7 - 4)) / 5",
            "8 + 2 * 5 - 3 / 1",
            "(2 + 3) ^ 2",
            "4 ^ 2 ^ 2",
            "100 / (5 * 2)",
            "3 - 10 / 2",
            "(7 + 3) * (4 + 2 ^ 3) - 5",
            "7 + )3 * 4(" // invalid
        };

        for (String expr : examples)
        {
            System.out.println("Input: " + expr);

            try
            {
                validateExpression(expr);

                String postfix = infixToPostfix(expr);
                System.out.println("Postfix: " + postfix);

                double result = evaluatePostfix(postfix);
                System.out.println("Result: " + result);

            }
            catch (Exception e)
            {
                System.out.println("Invalid expression: " + e.getMessage());
            }

            System.out.println("--------------------------------------------------");
        }
    }

    public static void validateExpression(String expr) throws Exception
    {
        Stack<Character> stack = new Stack<>();
        char prev = ' ';

        for (char c : expr.toCharArray())
        {
            if (c == ' ') continue;

            // Check parentheses
            if (c == '(') stack.push(c);
            else if (c == ')') {
                if (stack.isEmpty()) throw new Exception("Unbalanced parentheses");
                stack.pop();
            }

            // Check consecutive operators
            if ("+-*/^".indexOf(c) != -1)
            {
                if ("+-*/^".indexOf(prev) != -1 && prev != ' ')
                {
                    throw new Exception("Two operators in a row");
                }
            }

            prev = c;
        }

        if (!stack.isEmpty()) throw new Exception("Unbalanced parentheses");
    }

    public static String infixToPostfix(String expr) throws Exception
    {
        Stack<String> ops = new Stack<>();
        StringBuilder output = new StringBuilder();

        List<String> tokens = tokenize(expr);

        for (String token : tokens)
        {
            if (isNumber(token))
            {
                output.append(token).append(" ");
            }
            else if (isOperator(token))
            {
                while (!ops.isEmpty() &&
                        isOperator(ops.peek()) &&
                        (
                            (!token.equals("^") && PRECEDENCE.get(token) <= PRECEDENCE.get(ops.peek())) ||
                            (token.equals("^") && PRECEDENCE.get(token) < PRECEDENCE.get(ops.peek()))
                        )
                )
                {
                    output.append(ops.pop()).append(" ");
                }
                ops.push(token);
            }
            else if (token.equals("("))
            {
                ops.push(token);
            }
            else if (token.equals(")"))
            {
                while (!ops.isEmpty() && !ops.peek().equals("("))
                {
                    output.append(ops.pop()).append(" ");
                }
                if (ops.isEmpty()) throw new Exception("Mismatched parentheses");
                ops.pop(); // remove "("
            }
        }

        while (!ops.isEmpty())
        {
            if (ops.peek().equals("(")) throw new Exception("Mismatched parentheses");
            output.append(ops.pop()).append(" ");
        }

        return output.toString().trim();
    }

    public static double evaluatePostfix(String postfix) throws Exception
    {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix.split(" "))
        {
            if (isNumber(token))
            {
                stack.push(Double.parseDouble(token));
            }
            else if (isOperator(token))
            {
                if (stack.size() < 2) throw new Exception("Insufficient operands");

                double b = stack.pop();
                double a = stack.pop();

                switch (token) {
                    case "+" -> stack.push(a + b);
                    case "-" -> stack.push(a - b);
                    case "*" -> stack.push(a * b);
                    case "/" -> stack.push(a / b);
                    case "^" -> stack.push(Math.pow(a, b));
                }
            }
        }

        if (stack.size() != 1) throw new Exception("Invalid expression structure");
        return stack.pop();
    }

    private static boolean isNumber(String token)
    {
        return token.matches("\\d+(\\.\\d+)?");
    }

    private static boolean isOperator(String token)
    {
        return PRECEDENCE.containsKey(token);
    }

    private static List<String> tokenize(String expr)
    {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (char c : expr.toCharArray())
        {
            if (Character.isDigit(c) || c == '.')
            {
                number.append(c);
            }
            else
            {
                if (number.length() > 0)
                {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
                if (c == ' ') continue;
                tokens.add(String.valueOf(c));
            }
        }

        if (number.length() > 0) tokens.add(number.toString());
        return tokens;
    }
}