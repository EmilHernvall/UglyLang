UglyLang
========

Toy programming language. Work in progress. Most of what's in this document is
not yet implemented.

Features:

 * Static typing
 * First-class functions
 * Prototype-based objects
   * No implementation inheritence
   * Interfaces using type system

Built-in types:

 * string
 * int
 * boolean
 * array
 * objects

Statements:

 * declarations
 * if
 * while
 * type
 * compound_type
 * unpack

Operators
---------

Arithmetic:

     + Addition
     - Subtraction
     * Multiplication
     / Division
     ^ Exponentation
     % Modulus

Comparison:

    == Equals
    != Unequal
    > Larger than
    < Less than
    >= Larger than or equal
    <= Less than or equal

Logical:

These are overloaded and behave differently for different types. They
short-circuit for booleans, while they are bit-wise operators for integers.

    and
    or
    not
    xor

Subscript and indexing:

    []
    .

Priority:

    ()

Types
------------

A type can be given a name using type aliases:

    type MyFunc (string)(string, int, int);
    type MyFuncArr MyFunc[];

You can also declare compound types:

    compound_type LinkedList #Link (value:string, next:LinkedList),
                             #Terminator;

A compound type has multiple possible representations, which can be accessed
using the the unpack statement:

    LinkedList current = myList;
    int loop = 1;
    while loop == 1 {
        unpack current as #Link n {
            print(n.value);
            current = n.next;
        }

        unpack current as #Terminator {
            loop = 0;
        }
    }

Unpack guarantees that the code in the scope will only be executed if the
variable has the correct type.

Arrays
------

Arrays can be defined for any type by appending [].

    int[] arr = [ 2,3,5,7,11 ];

An empty array of specified size can be created using:

    int[] arr = array(100, 0);

In this case 100 is the size, and 0 is the default value.

An automatically growing array can be created using:

    int[] arr = autoarray(0);

0 is the default value.

Values can be set and retrieved using the subscript operator:

    arr[0] = 7;
    int foo = arr[0];

Objects
-------

An object is defined like this:

    (name:string, age:int, height:int) myObj =
        (name:"emil", age:26, height:188);

Not all keys of an object has to be declared in the type signature, but all
keys in the type signature has to be present in the instance of the object.

Objects are dereferenced using the subscript operator:

    string val2 = myTuple.name;

Named tuples are used to construct objects. An object is a named tuple with
fields containing functions. All other fields of the tuple can be accessed from
within it. Externally only the keys specified in the type signature will be
available. The named tuple type is thus the object interface.

    type Greeter (name: string,
                  setName: (void)(string),
                  setAge: (void)(int),
                  greet: (void)());

    (Greeter)(string,int) createGreeter =
        (Greeter)(string name_, int age_) {
            return (
                name: name,
                age: age,
                setName:(void)(string v) {
                    name = v;
                },
                setAge:(void)(int v) {
                    age = v;
                },
                greet:(void)() {
                    print("Hello " + name + "! You are " +
                          intToStr(age) + " this year.");
                }
            );
        };

    Greeter emil = createGreeter("Emil", 26);
    emil.name = "Emil Hernvall";
    emil.greet();

Functions
---------

A function signature looks like this:

    (return-type)(param-type param-name, ...)

Functions are always stored in a variable.

For example:

    (int)(int,int) fastexp =
        (int)(int num, int pow) {
            if (pow == 1) {
                return num;
            }
            if (pow % 2 == 0) {
                return fastexp(num*num, pow/2);
            }
            return num*fastexp(num*num, (pow - 1)/2);
        };

    (int)(int) factorial =
        (int)(int num) {
            if (num == 1) {
                return num;
            }
            return num*factorial(num-1);
        };

Control structures
------------------

If statements:

    if a == b {
        /* code */
    }

While statements:

    while a < len {
        /* code */
    }
