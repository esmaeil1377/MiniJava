package parser;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

import main.java.log.Log;
import main.java.code.generator.CodeGenerator;
import main.java.error.handler.ErrorHandler;
import scanner.token.Token;


public class Parser {
  private List<Rule> rules;
  private Stack<Integer> parsStack;
  private ParseTable parseTable;
  private scanner.LexicalAnalyzer lexicalAnalyzer;
  private CodeGenerator cg;

  public Parser() {
    parsStack = new Stack<Integer>();
    parsStack.push(0);
    try {
      parseTable = new ParseTable(Files.readAllLines(Paths.get("src/main/resources/parseTable")).get(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
    rules = new ArrayList<Rule>();
    try {
      for (String stringRule : Files.readAllLines(Paths.get("src/main/resources/Rules"))) {
        rules.add(new Rule(stringRule));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    cg = new CodeGenerator();
  }

  public void startParse(java.util.Scanner sc) {
    lexicalAnalyzer = new scanner.LexicalAnalyzer(sc);
    Token lookAhead = lexicalAnalyzer.getNextToken();
    boolean finish = false;
    Action currentAction;
    while (!finish) {
      try {
        Log.print(/*"lookahead : "+*/ lookAhead.toString() + "\t" + parsStack.peek());
//                log.print("state : "+ parsStack.peek());
        currentAction = parseTable.getActionTable(parsStack.peek(), lookAhead);
        Log.print(currentAction.toString());
        //log.print("");

        if (currentAction.action == act.shift){
          parsStack.push(currentAction.number);
          lookAhead = lexicalAnalyzer.getNextToken();
        } else if (currentAction.action == act.reduce){
          Rule rule = rules.get(currentAction.number);
          for (int i = 0; i < rule.RHS.size(); i++) {
            parsStack.pop();
          }

          Log.print(/*"state : " +*/ parsStack.peek() + "\t" + rule.LHS);
//                        log.print("LHS : "+rule.LHS);
          parsStack.push(parseTable.getGotoTable(parsStack.peek(), rule.LHS));
          Log.print(/*"new State : " + */parsStack.peek() + "");
//                        log.print("");
          try {
            cg.semanticFunction(rule.semanticAction, lookAhead);
          } catch (Exception e) {
            Log.print("Code Genetator Error");
          }
        } else if (currentAction.action == act.accept){
          finish = true;
        }

        Log.print("");

      } catch (Exception ignored) {

        ignored.printStackTrace();
//                boolean find = false;
//                for (NonTerminal t : NonTerminal.values()) {
//                    if (parseTable.getGotoTable(parsStack.peek(), t) != -1) {
//                        find = true;
//                        parsStack.push(parseTable.getGotoTable(parsStack.peek(), t));
//                        StringBuilder tokenFollow = new StringBuilder();
//                        tokenFollow.append(String.format("|(?<%s>%s)", t.name(), t.pattern));
//                        Matcher matcher = Pattern.compile(tokenFollow.substring(1)).matcher(lookAhead.toString());
//                        while (!matcher.find()) {
//                            lookAhead = LexicalAnalyzer.getNextToken();
//                        }
//                    }
//                }
//                if (!find)
//                    parsStack.pop();
      }


    }
    if (!ErrorHandler.isHasError()) {
      cg.printMemory();
    }

  }


}
