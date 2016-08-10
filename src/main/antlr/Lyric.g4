grammar Lyric;

program
    :   ((importExpr | exportExpr) (Nl | ';')+)* Nl* statement+
    ;

block
    :   '{' Nl* statement* '}'
    ;

classDefinition
    :   'class' Id typeParameters? extendsDefinition? Nl* '{' Nl* (classDeclaration (Nl | ';')+)* '}'
    ;

extendsDefinition
    :   (Extends Id typeParameters?)
    ;

classDeclaration
    :   declarationModifier* (constructor | declaration)
    ;

declarationModifier
    :   'static' | 'public' | 'private' | 'protected' | 'abstract'
    ;

statement
    :   (   declaration
        |   assignment
        |   expression
        |   classDefinition
        |   returnStatement
        |   ifStatement
        |   whileStatement
        ) (Nl | ';')+
    ;

ifStatement
    :   'if' LParen expression RParen Nl* (block | statement) elseStatement?
    ;

elseStatement
    :   'else' Nl* (block | statement)
    ;

whileStatement
    :   'while' LParen expression RParen Nl* (block | statement)
    ;

constructor
    :   Id LParen functionArgumentList? RParen Nl* block
    ;

returnStatement
    :   'return' expression
    ;

declaration
    :   Var Id (':' type)
    |   (Val | Var) Id (':' type)? ('=' Nl? expression)
    |   'function' Id LParen functionArgumentList? RParen (':' type)? Nl* block
    ;

functionLiteral
    :   LParen functionArgumentList? RParen (':' type)? Nl* RArrow Nl* (block | expression)
    ;

functionArgumentList
    :   Id (':' type)?
    |   functionArgumentList ',' Nl? Id (':' type)?
    ;

assignment
    :   postfixExpression assignmentOperator Nl? conditionalExpression
    ;

assignmentOperator
    :   '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
    ;

primaryExpression
    :   Id typeParameters*
    |   Constant
    |   StringLiteral
    |   NativeValue
    |   functionLiteral
    |   LParen expression RParen
    ;

postfixExpression
    :   primaryExpression
    |   postfixExpression '[' expression ']'
    |   postfixExpression '.' (Id typeParameters*)
    |   postfixExpression LParen argumentExpressionList? RParen
    ;

argumentExpressionList
    :   conditionalExpression
    |   argumentExpressionList ',' conditionalExpression
    ;

unaryExpression
    :   postfixExpression
    |   unaryOperator unaryExpression
    ;

unaryOperator
    :   '*' | '+' | '-' | '~' | '!'
    ;

infixExpression
    : unaryExpression
    | infixExpression Id unaryExpression
    ;

multiplicativeExpression
    :   infixExpression
    |   multiplicativeExpression '*' infixExpression
    |   multiplicativeExpression '/' infixExpression
    |   multiplicativeExpression '%' infixExpression
    ;

additiveExpression
    :   multiplicativeExpression
    |   additiveExpression (Plus | Minus) multiplicativeExpression
    ;

shiftExpression
    :   additiveExpression
    |   shiftExpression '<<' additiveExpression
    |   shiftExpression '>>' additiveExpression
    ;

relationalExpression
    :   shiftExpression
    |   relationalExpression LessThan shiftExpression
    |   relationalExpression GreaterThan shiftExpression
    |   relationalExpression LessThanEquals shiftExpression
    |   relationalExpression GreaterThanEquals shiftExpression
    ;

equalityExpression
    :   relationalExpression
    |   equalityExpression '==' relationalExpression
    |   equalityExpression '!=' relationalExpression
    ;

andExpression
    :   equalityExpression
    |   andExpression '&' equalityExpression
    ;

exclusiveOrExpression
    :   andExpression
    |   exclusiveOrExpression '^' andExpression
    ;

inclusiveOrExpression
    :   exclusiveOrExpression
    |   inclusiveOrExpression '|' exclusiveOrExpression
    ;

logicalAndExpression
    :   inclusiveOrExpression
    |   logicalAndExpression '&&' inclusiveOrExpression
    ;

logicalOrExpression
    :   logicalAndExpression
    |   logicalOrExpression '||' logicalAndExpression
    ;

conditionalExpression
    :   logicalOrExpression ('?' conditionalExpression ':' conditionalExpression)?
    ;

expression
    :   conditionalExpression
    ;

exportExpr
    :   'export' Default? (Id | declaration)
    ;

importExpr
    :   'import' (Id 'from')? StringLiteral
    ;


typeParameters
    :   LessThan typeList GreaterThan
    ;

boundedType
    : (Id Extends)? type Ellipses?
    ;

typeList
    :   boundedType
    |   typeList ',' boundedType
    ;

type
    :   Id typeParameters?
    |   NativeType
    ;

NativeValue
    :   NativeType (Nl | Whitespace)* NestedParens
    ;

NativeType
    :   NativePrefix Id
    ;

NativePrefix
    :   'N' (LArrow | RArrow)
    ;

Nl
    :   '\r'? '\n'
    ;

Val : 'val';
Var : 'var';
Plus : '+';
Minus : '-';
LessThan : '<';
GreaterThan : '>';
LessThanEquals : '<=';
GreaterThanEquals : '>=';
Default : 'default';
Ellipses : '...';
Extends : 'extends';

LParen
    : '('
    ;

RParen
    : ')'
    ;

LArrow
    :   '<-'
    ;

RArrow
    :   '->'
    ;

Id
    :   IdentifierNondigit
        (   IdentifierNondigit
        |   Digit
        )*
    ;

Constant
    :   Digit+
    ;

StringLiteral
    :  '\'' SCharSequence? '\''
    ;

fragment SCharSequence
    :   SChar+
    ;

fragment SChar
    :   ~['\\\r\n]
    |   EscapeSequence
    |   '\\\n'   // Added line
    |   '\\\r\n' // Added line
    ;

fragment EscapeSequence
    :   '\\' ['"?abfnrtv\\]
    ;

fragment NestedParens
    :   (Nl | Whitespace)* '(' ( ~('(' | ')') | NestedParens )* ')'
    ;

fragment NestedBrackets
    :   (Nl | Whitespace)* '{' ( ~[{}] | NestedBrackets )* '}'
    ;

fragment IdentifierNondigit
    :   Nondigit
    ;

fragment Nondigit
    :   [a-zA-Z_@]
    ;

fragment Digit
    :   [0-9]
    ;

BlockComment
    :   '#-' .*? '-#'
        -> skip
    ;

LineComment
    :   '#' ~[\r\n]*
        -> skip
    ;

Whitespace
    :   [ \t]+
        -> skip
    ;