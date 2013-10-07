UglyLang
========

Toy programming language. Work in progress.

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
 * assignment
 * type
 * compound_type
 * if, elif, else
 * while
 * unpack

Planned Standard Library:

 * IO
     * File
     * Network
     * HTTP
 * Data structures
    * List
         * Array
         * CopyOnWriteArray
         * LinkedList
    * Set
         * AVLTree
         * Hashtable
    * Map
         * AVLTree
         * Hashtable
    * MultiMap
         * Hashtable
 * Date and Time
 * Regex
 * SQL

Operators
---------

Arithmetic:

     + Addition
     - Subtraction
     * Multiplication
     / Division
     ^ Exponentation (unimplemented)
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

Values can be set and retrieved using the index operator:

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

    string val2 = obj.name;

The fields of an object can be function. In that case, the fields of the object
are accessible from within the functions.

    type Greeter (name: string,
                  setName: (void)(string),
                  setAge: (void)(int),
                  greet: (void)());

    (Greeter)(string,int) createGreeter =
    (Greeter)(string name, int age) {
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
                      age.str + " this year.");
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
        if pow == 1 {
            return num;
        }
        if pow % 2 == 0 {
            return fastexp(num*num, pow/2);
        }
        return num*fastexp(num*num, (pow - 1)/2);
    };

    (int)(int) factorial =
    (int)(int num) {
        if num == 1 {
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
    elif a == c {
        /* code */
    }
    else {
        /* code */
    }

While statements:

    while a < len {
        /* code */
    }
